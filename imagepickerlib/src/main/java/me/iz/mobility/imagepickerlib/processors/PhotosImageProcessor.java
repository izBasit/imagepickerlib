package me.iz.mobility.imagepickerlib.processors;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by ibasit on 8/24/2016.
 */
public class PhotosImageProcessor extends ImageProcessorImpl {


    public PhotosImageProcessor(Context mContext, Uri uri) {
        super(mContext, uri);
    }

    @Override
    public String getImage() {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {
                column
        };

        try {
            cursor = mContext.getContentResolver().query(uri, projection, null, null,
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
