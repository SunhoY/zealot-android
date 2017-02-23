package io.harry.zealot.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.harry.zealot.ZealotApplication;
import io.harry.zealot.helper.FirebaseHelper;
import io.harry.zealot.model.Gag;

public class GagService {

    private static final String FILE_NAME_FIELD = "fileName";
    private static final String VERIFIED_FIELD = "verified";
    private static final String GAGS_REFERENCE = "gags";
    private static final int SAMPLE_SIZE = 100;

    @Inject
    FirebaseHelper firebaseHelper;

    public GagService(Context context) {
        ((ZealotApplication) context).getZealotComponent().inject(this);
    }

    public void getGags(final int requestCount, boolean verified, final ServiceCallback<List<Gag>> serviceCallback) {
        DatabaseReference gagReference = firebaseHelper.getDatabaseReference(GAGS_REFERENCE);

        Query query = gagReference.orderByChild(VERIFIED_FIELD).equalTo(verified).limitToLast(SAMPLE_SIZE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                List<Gag> result = new ArrayList<>();

                for(DataSnapshot child : children) {
                    Gag gag = child.getValue(Gag.class);
                    gag.key = child.getKey();
                    result.add(gag);
                }

                //TODO Powermock integration with robolectric is now broken
                //todo fix it when robolectric 3.3 is released
                Collections.shuffle(result);

                serviceCallback.onSuccess(result.subList(0, requestCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getGagImageUris(final List<String> fileNames, final ServiceCallback<List<Uri>> serviceCallback) {
        StorageReference storageReference = firebaseHelper.getStorageReference(GAGS_REFERENCE);

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

        StorageReference gagsReference = firebaseHelper.getStorageReference(GAGS_REFERENCE);
        StorageReference imageReference = gagsReference.child(fileName);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        UploadTask uploadTask = imageReference.putBytes(byteArrayOutputStream.toByteArray());
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                DatabaseReference gagsRef = firebaseHelper.getDatabaseReference(GAGS_REFERENCE);
                DatabaseReference newlyCreatedChild = gagsRef.push();

                ImmutableMap value = ImmutableMap.of(FILE_NAME_FIELD, fileName, VERIFIED_FIELD, false);

                newlyCreatedChild.setValue(value);

                serviceCallback.onSuccess(null);
            }
        });
    }

    public void verifyGag(Gag gag) {
        DatabaseReference gagReference = firebaseHelper.getDatabaseReference(GAGS_REFERENCE);
        DatabaseReference targetGagReference = gagReference.child(gag.key);
        targetGagReference.child(VERIFIED_FIELD).setValue(true);
    }

    public void rejectGag(Gag gag) {
        StorageReference storageReference = firebaseHelper.getStorageReference(GAGS_REFERENCE);
        storageReference.child(gag.fileName).delete();

        DatabaseReference databaseReference = firebaseHelper.getDatabaseReference(GAGS_REFERENCE);
        databaseReference.child(gag.key).removeValue();
    }
}
