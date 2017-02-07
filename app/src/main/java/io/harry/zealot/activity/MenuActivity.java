package io.harry.zealot.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.harry.zealot.R;
import io.harry.zealot.helper.BitmapHelper;
import io.harry.zealot.helper.PermissionHelper;
import io.harry.zealot.service.GagService;
import io.harry.zealot.service.ServiceCallback;

public class MenuActivity extends ZealotBaseActivity {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        zealotComponent.inject(this);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.start_button)
    public void onStartClick() {
        if(!permissionHelper.hasPermission(Manifest.permission.CAMERA)) {
            //todo: figure out how to test this
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_FOR_CAMERA);
        }else {
            startActivity(new Intent(this, TestAjaeActivity.class));
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
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_PHOTO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //todo: not tested
        switch (requestCode) {
            case REQUEST_FOR_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_PHOTO);
                } else {
                    Toast.makeText(this, R.string.storage_permission_needed, Toast.LENGTH_LONG).show();
                }

                return;
            }

            case REQUEST_FOR_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(this, TestAjaeActivity.class));
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

        gagService.uploadGag(scaledBitmap, new ServiceCallback() {
            @Override
            public void onSuccess(Object result) {
                Toast.makeText(MenuActivity.this, R.string.upload_complete, Toast.LENGTH_LONG).show();
            }
        });
    }

    class BitmapSize {
        int width;
        int height;

        BitmapSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
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
