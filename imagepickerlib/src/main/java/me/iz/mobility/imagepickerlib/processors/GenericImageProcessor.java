package me.iz.mobility.imagepickerlib.processors;

import android.content.Context;
import android.net.Uri;

/**
 * Created by ibasit on 8/23/2016.
 */
public class GenericImageProcessor extends ImageProcessorImpl {

    public GenericImageProcessor(Context mContext, Uri uri) {
        super(mContext, uri);
    }

    @Override
    public String getImage() {

        String path = uri.getPath();
        int pos = path.indexOf("/storage");

        imagePath = path.substring(pos);


        return imagePath;
    }


}
