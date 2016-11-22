package me.iz.mobility.imagepickerlib.processors;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by ibasit on 8/26/2016.
 */
public class GalleryImageProcessor extends ImageProcessorImpl {
    public GalleryImageProcessor(Context mContext, Uri uri) {
        super(mContext, uri);
    }

    @Override
    public String getImage() {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(mContext, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        imagePath = cursor.getString(column_index);
        cursor.close();
        return imagePath;
    }

}
