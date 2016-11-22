package me.iz.mobility.imagepickerlib.processors;

import android.content.Context;
import android.net.Uri;

/**
 * Created by ibasit on 8/23/2016.
 */
public class FileImageProcessor extends
    ImageProcessorImpl {
    public FileImageProcessor(Context mContext, Uri uri) {
        super(mContext, uri);
    }

    @Override
    public String getImage() {

        imagePath = uri.getPath();
        return imagePath;
    }
}
