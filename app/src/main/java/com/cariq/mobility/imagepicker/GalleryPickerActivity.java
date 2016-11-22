package com.cariq.mobility.imagepicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;
import java.io.File;
import me.iz.mobility.imagepickerlib.ImagePickerActivity;

public class GalleryPickerActivity extends ImagePickerActivity {

    private static final String TAG = "GalleryPickerActivity";

    @BindView(R.id.ivPic)
    ImageView ivPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_picker);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnPick)
    public void pickFromGallery() {

        if(!hasPermission()){
            Toast.makeText(GalleryPickerActivity.this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
            return;
        }

        pickImageFromGallery();

    }

    @OnClick(R.id.btnCapture)
    public void captureFromCamera() {
        if(!hasPermission()) {
            Toast.makeText(GalleryPickerActivity.this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
            return;
        }

        captureImageFromCamera("cariq");
    }


    @SuppressLint("InlinedApi")
    private boolean hasPermission() {

        final boolean[] isPermissionGranted = new boolean[1];
        RxPermissions.getInstance(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .subscribe(permission -> { // will emit 2 Permission objects
                    if (permission) {
                        Log.d(TAG, "hasPermission: Permission granted");
//                        Timber.d("Permission granted for %s",permission.name);
                        // `permission.name` is granted !
                        isPermissionGranted[0] = true;
                    } else {
//                        show
//                        Timber.d("Permission denied for %s",permission.name);
                        Log.w(TAG, "hasPermission: Permission denied ");
                        isPermissionGranted[0] = false;
                    }
                }, error -> {
                    Log.e(TAG, "hasPermission: "+error.getMessage());
                    isPermissionGranted[0] = false;
                });

        return isPermissionGranted[0];
    }


    @Override
    public void onImagePathObtained(String imagePath) {
        File mFile = new File(imagePath);

        Picasso.with(this).load(mFile).into(ivPic);
        Log.i(TAG, "onActivityResult: Image path obtained "+imagePath);
    }

    @Override
    public void onImagePathError(String errorMessage) {
        Toast.makeText(GalleryPickerActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
