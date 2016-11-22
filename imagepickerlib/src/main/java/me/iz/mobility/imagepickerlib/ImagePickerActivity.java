package me.iz.mobility.imagepickerlib;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.Date;
import java.util.Locale;
import me.iz.mobility.imagepickerlib.processors.ImageProcessor;
import me.iz.mobility.imagepickerlib.processors.ImageProcessorFactory;
import me.iz.mobility.imagepickerlib.utils.DateParser;

/**
 * Created by ibasit on 8/24/2016.
 */
public abstract class ImagePickerActivity extends AppCompatActivity {

    private static final String TAG = "ImagePickerActivity";

    private static final String DATE_CAMERA_INTENT_STARTED_STATE = "me.iz.mobility.CameraIntentHelperActivity.dateCameraIntentStarted";
    private static final String CAMERA_PIC_URI_STATE = "me.iz.mobility.CameraIntentHelperActivity.CAMERA_PIC_URI_STATE";
    private static final String PHOTO_URI_STATE = "me.iz.mobility.CameraIntentHelperActivity.PHOTO_URI_STATE";
    private static final String ROTATE_X_DEGREES_STATE = "me.iz.mobility.CameraIntentHelperActivity.ROTATE_X_DEGREES_STATE";

    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private final static int GALLERY_INTENT_CALLED = 999;
    private final static int GALLERY_KITKAT_INTENT_CALLED = 899;

    /**
     * Date and time the camera intent was started.
     */
    private Date dateCameraIntentStarted = null;

    /**
     * Default location where we want the photo to be ideally stored.
     */
    private Uri preDefinedCameraUri = null;
    /**
     * Retrieved location of the photo.
     */
    private Uri photoUri = null;

    /**
     * Orientation of the retrieved photo.
     */
    protected int rotateXDegrees = 0;


    public static boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;


    protected void pickImageFromGallery() {
        if (isKitKat) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.str_select_image)), GALLERY_KITKAT_INTENT_CALLED);
//            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.str_select_image)), GALLERY_INTENT_CALLED);
        }
    }

    /**
     * Method to capture image
     * @param imageName
     */
    protected void captureImageFromCamera(String imageName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                // NOTE: Do NOT SET: intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPicUri)
                // on Samsung Galaxy S2/S3/.. for the following reasons:
                // 1.) it will break the correct picture orientation
                // 2.) the photo will be stored in two locations (the given path and, additionally, in the MediaStore)
                String manufacturer = Build.MANUFACTURER.toLowerCase(Locale.ENGLISH);
                String model = Build.MODEL.toLowerCase(Locale.ENGLISH);
                String buildType = Build.TYPE.toLowerCase(Locale.ENGLISH);
                String buildDevice = Build.DEVICE.toLowerCase(Locale.ENGLISH);
                String buildId = Build.ID.toLowerCase(Locale.ENGLISH);

                boolean setPreDefinedCameraUri = false;
                if (!(manufacturer.contains("samsung")) && !(manufacturer.contains("sony"))) {
                    setPreDefinedCameraUri = true;
                }
                if (manufacturer.contains("samsung") && model.contains("galaxy nexus")) {
                    setPreDefinedCameraUri = true;
                }
                if (manufacturer.contains("samsung") && model.contains("gt-n7000") && buildId.contains("imm76l")) {
                    setPreDefinedCameraUri = true;
                }

                if (buildType.contains("userdebug") && buildDevice.contains("ariesve")) {  //TESTED
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("crespo")) {   //TESTED
                    setPreDefinedCameraUri = true;
                }

                if (buildType.contains("samsung") && model.contains("sm-t211")) {
                    setPreDefinedCameraUri = true;
                }

                ///////////////////////////////////////////////////////////////////////////
                // TEST: CyanogenMod
                if (buildType.contains("userdebug") && buildDevice.contains("cooper")) {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("t0lte")) {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("gt-i9100")) {
                    setPreDefinedCameraUri = true;
                }
                ///////////////////////////////////////////////////////////////////////////


                dateCameraIntentStarted = new Date();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (setPreDefinedCameraUri) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, imageName);
                    preDefinedCameraUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, preDefinedCameraUri);
                }
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                onImagePathError(e.getMessage());
            }
        } else {
            onImagePathError("No SD card found");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                ImageProcessor imageProcessor = new ImageProcessorFactory().getCameraProcessor(this,
                        data, dateCameraIntentStarted, rotateXDegrees, preDefinedCameraUri);
                String imagePath = imageProcessor.getImage();
                if(imagePath == null) {
                    onImagePathError("Image path not obtained");
                    Log.d(TAG, "onActivityResult: Image path not obtained");
                    return;
                }
                onImagePathObtained(imagePath);
                break;
            }
            case GALLERY_KITKAT_INTENT_CALLED:
            case GALLERY_INTENT_CALLED:
                Log.i(TAG, "onActivityResult: Returned from gallery");
                if (data != null) {
                    Uri uri = data.getData();
                    ImageProcessor imgProcessor = new ImageProcessorFactory().getImageProcessor(this,uri);
                    if(imgProcessor == null) {
                        onImagePathError("No compatible processor for file type");
                        return;
                    }
                    String imagePath = imgProcessor.getImage();

                    if(imagePath == null) {
                        onImagePathError("Image path not obtained");
                        Log.d(TAG, "onActivityResult: Image path not obtained");
                        return;
                    }

                    onImagePathObtained(imagePath);
                } else {
                    Log.w(TAG, "onActivityResult: Intent data is null!");
                }
                break;
        }
    }

    public abstract void onImagePathObtained(String imagePath);

    public abstract void onImagePathError(String errorMessage);


    /**
     * Saves the current state of the activity.
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (dateCameraIntentStarted != null) {
            savedInstanceState.putString(DATE_CAMERA_INTENT_STARTED_STATE, DateParser
                .dateToString(dateCameraIntentStarted));
        }
        if (preDefinedCameraUri != null) {
            savedInstanceState.putString(CAMERA_PIC_URI_STATE, preDefinedCameraUri.toString());
        }
        if (photoUri != null) {
            savedInstanceState.putString(PHOTO_URI_STATE, photoUri.toString());
        }
        savedInstanceState.putInt(ROTATE_X_DEGREES_STATE, rotateXDegrees);
    }

    /**
     * Reinitializes a saved state of the activity.
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(DATE_CAMERA_INTENT_STARTED_STATE)) {
            dateCameraIntentStarted = DateParser.stringToDate(savedInstanceState.getString(DATE_CAMERA_INTENT_STARTED_STATE));
        }
        if (savedInstanceState.containsKey(CAMERA_PIC_URI_STATE)) {
            preDefinedCameraUri = Uri.parse(savedInstanceState.getString(CAMERA_PIC_URI_STATE));
        }
        if (savedInstanceState.containsKey(PHOTO_URI_STATE)) {
            photoUri = Uri.parse(savedInstanceState.getString(PHOTO_URI_STATE));
        }
        rotateXDegrees = savedInstanceState.getInt(ROTATE_X_DEGREES_STATE);
    }
}
