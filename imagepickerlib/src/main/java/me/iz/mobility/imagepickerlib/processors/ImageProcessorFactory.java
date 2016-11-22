package me.iz.mobility.imagepickerlib.processors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.util.Log;
import java.util.Date;

/**
 * Created by ibasit on 8/23/2016.
 */
public class ImageProcessorFactory {
    private static final String TAG = "ImageProcessorFactory";

    public static boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    @SuppressLint("NewApi")
    public ImageProcessor getImageProcessor(Context mContext, Uri uri) {
        Log.i(TAG, "getImageProcessor: URI " + uri.toString());
        Log.i(TAG, "getImageProcessor: Authority " + uri.getAuthority());
        Log.i(TAG, "getImageProcessor: Scheme " + uri.getScheme());
        if (isKitKat && DocumentsContract.isDocumentUri(mContext, uri)) {

            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                Log.d(TAG, "getImageProcessor: external storage processor");
                return new ExternalStorageDocumentImageProcessor(mContext, uri);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Log.d(TAG, "getImageProcessor: download document image processor");
                return new DownloadsDocumentImageProcessor(mContext, uri);
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                Log.d(TAG, "getImageProcessor: media document image processor");
                return new MediaDocumentImageProcessor(mContext, uri);
            } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                Log.d(TAG, "getImageProcessor: google drive processor");
                return new GoogleDriveImageProcessor(mContext, uri);
            }
        }
        if ("com.google.android.apps.photos.contentprovider".equals(uri.getAuthority())) {
            Log.d(TAG, "getImageProcessor: Photos app processor");
            return new PhotosImageProcessor(mContext, uri);
        }

        if ("media".equalsIgnoreCase(uri.getAuthority())) {
            Log.d(TAG, "getImageProcessor: Gallery image processor");
            return new GalleryImageProcessor(mContext, uri);
//            return new MediaDocumentImageProcessor(mContext,uri);
        }

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.d(TAG, "getImageProcessor: Generic image processor");
            return new GenericImageProcessor(mContext, uri);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.d(TAG, "getImageProcessor: file image processor");
            return new FileImageProcessor(mContext, uri);
        }

        Log.w(TAG, "getImageProcessor: File type doesn't match " + uri.toString());
        return null;

    }

    public ImageProcessor getCameraProcessor(Context mContext, Intent data, Date dateCameraIntentStarted,
                                             int rotateXDegrees, Uri preDefinedCameraUri) {

        return new ProcessImageFromCamera(data, mContext, dateCameraIntentStarted, rotateXDegrees, preDefinedCameraUri);
    }
}
