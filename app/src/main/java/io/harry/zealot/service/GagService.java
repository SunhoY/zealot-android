package io.harry.zealot.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.harry.zealot.ZealotApplication;
import io.harry.zealot.helper.FirebaseHelper;
import io.harry.zealot.model.Gag;

public class GagService {

    @Inject
    FirebaseHelper firebaseHelper;

    public GagService(Context context) {
        ((ZealotApplication) context).getZealotComponent().inject(this);
    }

    public void getGagImageFileNames(final ServiceCallback<List<String>> serviceCallback) {
        DatabaseReference gagReference = firebaseHelper.getDatabaseReference("gags");
        gagReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                List<String> result = new ArrayList<>();

                for(DataSnapshot child : children) {
                    result.add(child.getValue(Gag.class).fileName);
                }

                serviceCallback.onSuccess(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getGagImageUris(final List<String> fileNames, final ServiceCallback<List<Uri>> serviceCallback) {
        StorageReference storageReference = firebaseHelper.getStorageReference("gags");

        final List<Uri> imageUris = new ArrayList<>();

        for(String fileName : fileNames) {
            StorageReference file = storageReference.child(fileName);
            file.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    Uri downloadUrl = storageMetadata.getDownloadUrl();
                    if(downloadUrl == null) {
                        return;
                    }

                    imageUris.add(downloadUrl);
                    if(imageUris.size() == fileNames.size()) {
                        serviceCallback.onSuccess(imageUris);
                    }
                }
            });
        }
    }

    public void uploadGag(Bitmap bitmap, final ServiceCallback<Void> serviceCallback) {
        final String fileName = String.valueOf(new DateTime().getMillis()) + ".jpg";

        StorageReference gagsReference = firebaseHelper.getStorageReference("gags");
        StorageReference imageReference = gagsReference.child(fileName);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        UploadTask uploadTask = imageReference.putBytes(byteArrayOutputStream.toByteArray());
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                DatabaseReference gagsRef = firebaseHelper.getDatabaseReference("gags");
                DatabaseReference newlyCreatedChild = gagsRef.push();

                ImmutableMap value = ImmutableMap.of("fileName", fileName);

                newlyCreatedChild.setValue(value);

                serviceCallback.onSuccess(null);
            }
        });
    }
}
