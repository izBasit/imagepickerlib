package me.iz.mobility.imagepickerlib.processors;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

/**
 * Created by ibasit on 8/23/2016.
 */
public class DownloadsDocumentImageProcessor extends ImageProcessorImpl {


    public DownloadsDocumentImageProcessor(Context mContext, Uri uri) {
        super(mContext, uri);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public String getImage() {
        String id = DocumentsContract.getDocumentId(uri);
        Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        Cursor cursor = null;
        String column = "_data";
        String[] projection = {
                column
        };

        try {
            cursor = mContext.getContentResolver().query(contentUri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                imagePath = cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return imagePath;
    }
}
