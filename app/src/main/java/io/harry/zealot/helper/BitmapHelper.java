package io.harry.zealot.helper;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class BitmapHelper {

    private ContentResolver contentResolver;

    public BitmapHelper(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public Bitmap getBitmapByURI(Uri uri) {
        Cursor cursor = contentResolver.query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);

        String decodableImage = "";
        if(cursor.moveToFirst()) {
            decodableImage = cursor.getString(0);
        }

        cursor.close();

        return BitmapFactory.decodeFile(decodableImage);
    }

    public Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }
}
