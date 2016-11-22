package me.iz.mobility.imagepickerlib.processors;

import android.content.Context;
import android.net.Uri;

/**
 * Created by ibasit on 8/23/2016.
 */
public abstract class ImageProcessorImpl implements
    ImageProcessor {

    protected Context mContext;

    protected Uri uri;

    protected String imagePath;

    public ImageProcessorImpl(Context mContext, Uri uri) {
        this.uri = uri;
        this.mContext = mContext;
    }
}
