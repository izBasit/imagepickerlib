package me.iz.mobility.imagepickerlib.processors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ibasit on 8/23/2016.
 */
public class GoogleDriveImageProcessor extends ImageProcessorImpl {

    private static final String TAG = "GDriveImgProcessor";

    public GoogleDriveImageProcessor(Context mContext, Uri uri) {
        super(mContext, uri);
    }


    @Override
    public String getImage() {
        Bitmap bitmap = null;
        try {
             bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(bitmap == null) {

            Log.d(TAG, "getImage: Bitmap is null");
            return null;
        }

        String imageName = String.valueOf(new Date().getTime());
        File sd = Environment.getExternalStorageDirectory();
        File dest = new File(sd, imageName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            imagePath = dest.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }



        return imagePath;
    }
}
