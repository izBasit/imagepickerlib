package me.iz.mobility.imagepickerlib.processors;

import android.content.Context;
import android.content.Intent;

/**
 * Created by ibasit on 8/24/2016.
 */
public abstract class CameraImageProcessorImpl implements ImageProcessor {

    protected Context mContext;

    protected Intent data;

    protected String imagePath;

    public CameraImageProcessorImpl(Intent data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }
}
