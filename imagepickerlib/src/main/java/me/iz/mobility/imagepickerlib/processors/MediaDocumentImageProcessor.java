package me.iz.mobility.imagepickerlib.processors;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Created by ibasit on 8/23/2016.
 */
public class MediaDocumentImageProcessor extends
    ImageProcessorImpl {
    public MediaDocumentImageProcessor(Context mContext, Uri uri) {
        super(mContext, uri);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public String getImage() {
        String docId = DocumentsContract.getDocumentId(uri);
        String[] split = docId.split(":");
        String type = split[0];

        Uri contentUri = null;
        if ("image".equals(type)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        String selection = "_id=?";
        String[] selectionArgs = new String[]{
                split[1]
        };

        Cursor cursor = null;
        String column = "_data";
        String[] projection = {
                column
        };

        try {
            cursor = mContext.getContentResolver().query(contentUri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                imagePath = cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return imagePath;
    }
}
