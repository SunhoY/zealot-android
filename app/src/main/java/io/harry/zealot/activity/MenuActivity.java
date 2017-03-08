package io.harry.zealot.activity;

import android.Manifest;
import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.harry.zealot.R;
import io.harry.zealot.dialog.DialogService;
import io.harry.zealot.helper.BitmapHelper;
import io.harry.zealot.helper.PermissionHelper;
import io.harry.zealot.service.GagService;
import io.harry.zealot.service.ServiceCallback;
import io.harry.zealot.wrapper.SharedPreferencesWrapper;

public class MenuActivity extends ZealotBaseActivity implements Animator.AnimatorListener {
    public static final int PICK_PHOTO = 9;
    public static final int MAX_WIDTH = 720;
    private static final int REQUEST_FOR_READ_EXTERNAL_STORAGE = 0;
    private static final int REQUEST_FOR_CAMERA = 1;

    @Inject
    BitmapHelper bitmapHelper;
    @Inject
    GagService gagService;
    @Inject
    PermissionHelper permissionHelper;
    @Inject
    DialogService dialogService;
    @Inject
    SharedPreferencesWrapper sharedPreferencesWrapper;

    @BindView(R.id.intro)
    LottieAnimationView intro;
    @BindView(R.id.start_button)
    Button start;
    @BindView(R.id.upload_button)
    Button upload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        zealotComponent.inject(this);

        ButterKnife.bind(this);

        intro.setImageAssetsFolder("images");
        intro.addAnimatorListener(this);
    }

    @OnClick(R.id.start_button)
    public void onStartClick() {
        if(!permissionHelper.hasPermission(Manifest.permission.CAMERA)) {
            //todo: figure out how to test this
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_FOR_CAMERA);
        } else {
            startNextActivity(sharedPreferencesWrapper.getSharedPreferences());
        }
    }

    @OnClick(R.id.upload_button)
    public void onUploadClick() {
        if (!permissionHelper.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //todo: figure out how to test this
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_FOR_READ_EXTERNAL_STORAGE);
        }
        else {
            startPhotoPickActivity();
        }
    }

    @OnClick(R.id.admin)
    public void onAdminClick() {
        Intent intent = new Intent(this, CheckAdminActivity.class);

        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FOR_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPhotoPickActivity();
                } else {
                    Toast.makeText(this, R.string.storage_permission_needed, Toast.LENGTH_LONG).show();
                }

                return;
            }

            case REQUEST_FOR_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNextActivity(sharedPreferencesWrapper.getSharedPreferences());
                } else {
                    Toast.makeText(this, R.string.camera_permission_needed, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null) {
            return;
        }

        Uri selectedImage = data.getData();

        Bitmap bitmap = bitmapHelper.getBitmapByURI(selectedImage);

        BitmapSize size = calculateSize(bitmap.getWidth(), bitmap.getHeight(), MAX_WIDTH);

        Bitmap scaledBitmap = bitmapHelper.scaleBitmap(bitmap, size.width, size.height);

        final ProgressDialog progressDialog = dialogService.getProgressDialog(this, "아재력을 퍼뜨리고 있습니다.");
        progressDialog.show();

        gagService.uploadGag(scaledBitmap, new ServiceCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(MenuActivity.this, R.string.upload_complete, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        start.setVisibility(View.VISIBLE);
        upload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    private class BitmapSize {
        int width;
        int height;

        BitmapSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    private void startNextActivity(SharedPreferences sharedPreferences) {
        if(sharedPreferences.getBoolean(SharedPreferencesWrapper.TUTORIAL_SEEN, false)) {
            startActivity(new Intent(this, TestAjaeActivity.class));
        }
        else {
            startActivity(new Intent(this, TutorialActivity.class));
        }
    }

    private void startPhotoPickActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        startActivityForResult(intent, PICK_PHOTO);
    }

    private BitmapSize calculateSize(int width, int height, int maxWidth) {
        if(width < maxWidth) {
            return new BitmapSize(width, height);
        }

        double scale = (double) maxWidth / (double) width;

        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        return new BitmapSize(newWidth, newHeight);
    }
}
