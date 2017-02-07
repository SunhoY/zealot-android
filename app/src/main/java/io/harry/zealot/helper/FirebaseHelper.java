package io.harry.zealot.helper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseHelper {
    public DatabaseReference getDatabaseReference(String child) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        return firebaseDatabase.getReference(child);
    }

    public StorageReference getStorageReference(String child) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        return firebaseStorage.getReference(child);
    }
}
