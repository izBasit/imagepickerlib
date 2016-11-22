package me.iz.mobility.imagepickerlib.processors;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import java.io.File;
import java.util.Date;

/**
 * Created by ibasit on 8/24/2016.
 */
public class ProcessImageFromCamera extends CameraImageProcessorImpl {

    public static boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    /**
     * Date and time the camera intent was started.
     */
    protected Date dateCameraIntentStarted = null;
    /**
     * Orientation of the retrieved photo.
     */
    protected int rotateXDegrees = 0;

    /**
     * Default location where we want the photo to be ideally stored.
     */
    protected Uri preDefinedCameraUri = null;

    public ProcessImageFromCamera(Intent data, Context mContext, Date dateCameraIntentStarted, int rotateXDegrees, Uri preDefinedCameraUri) {
        super(data, mContext);
        this.dateCameraIntentStarted = dateCameraIntentStarted;
        this.rotateXDegrees = rotateXDegrees;
        this.preDefinedCameraUri = preDefinedCameraUri;
    }

    @Override
    public String getImage() {
        if (!isKitKat) {
            Cursor myCursor = null;
            Date dateOfPicture = null;
            Uri photoUri = null;
            try {
                // Create a Cursor to obtain the file Path for the large image
                String[] largeFileProjection = {MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.ORIENTATION,
                        MediaStore.Images.ImageColumns.DATE_TAKEN};
                String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
                myCursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        largeFileProjection,
                        null, null,
                        largeFileSort);
                myCursor.moveToFirst();
                // This will actually give you the file path location of the image.
                String largeImagePath = myCursor.getString(myCursor
                        .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                photoUri = Uri.fromFile(new File(largeImagePath));
                dateOfPicture = new Date(myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.
                        Images.ImageColumns.DATE_TAKEN)));
                if (dateOfPicture.after(dateCameraIntentStarted)) {
                    rotateXDegrees = myCursor.getInt(myCursor
                            .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION));
                } else {
                    photoUri = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (myCursor != null && !myCursor.isClosed()) {
                    myCursor.close();
                }
            }

            if (photoUri == null) {
                try {
                    photoUri = data.getData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (photoUri == null) {
                photoUri = preDefinedCameraUri;
            }

            photoUri = getFileUriFromContentUri(photoUri);
//            preDefinedCameraUri = getFileUriFromContentUri(preDefinedCameraUri);

            if (photoUri != null) {
                imagePath = photoUri.getPath();
            } else {
                return null;
            }
        } else {
            String photoPath = MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    (Bitmap) data.getExtras().get("data"), "Title", null);

            Uri photoUri = getFileUriFromContentUri(Uri.parse(photoPath));
            imagePath = photoUri.getPath();
        }

        return imagePath;
    }

    protected Uri getFileUriFromContentUri(Uri cameraPicUri) {
        try {
            if (cameraPicUri != null
                    && cameraPicUri.toString().startsWith("content")) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = mContext.getContentResolver().query(cameraPicUri, proj, null, null, null);
                cursor.moveToFirst();
                // This will actually give you the file path location of the image.
                String largeImagePath = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                cursor.close();

                return Uri.fromFile(new File(largeImagePath));
            }
            return cameraPicUri;
        } catch (Exception e) {
            return cameraPicUri;
        }
    }
}
