package me.iz.mobility.imagepickerlib.processors;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by ibasit on 8/23/2016.
 */
public class ExternalStorageDocumentImageProcessor extends ImageProcessorImpl {

    public ExternalStorageDocumentImageProcessor(Context mContext, Uri uri) {
        super(mContext, uri);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public String getImage() {

        String docId = DocumentsContract.getDocumentId(uri);
        String[] split = docId.split(":");
        String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
            imagePath = Environment.getExternalStorageDirectory() + "/" + split[1];
        } else {
            Pattern DIR_SEPORATOR = Pattern.compile("/");
            Set<String> rv = new HashSet<>();
            String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
            String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
            String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
            if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                if (TextUtils.isEmpty(rawExternalStorage)) {
                    rv.add("/storage/sdcard0");
                } else {
                    rv.add(rawExternalStorage);
                }
            } else {
                String rawUserId;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    rawUserId = "";
                } else {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String[] folders = DIR_SEPORATOR.split(path);
                    String lastFolder = folders[folders.length - 1];
                    boolean isDigit = false;
                    try {
                        Integer integer = Integer.valueOf(lastFolder);
                        isDigit = true;
                    } catch (NumberFormatException ignored) {
                    }
                    rawUserId = isDigit ? lastFolder : "";
                }
                if (TextUtils.isEmpty(rawUserId)) {
                    rv.add(rawEmulatedStorageTarget);
                } else {
                    rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                }
            }
            if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                Collections.addAll(rv, rawSecondaryStorages);
            }
            String[] temp = rv.toArray(new String[rv.size()]);

            for (int i = 0; i < temp.length; i++) {
                File tempf = new File(temp[i] + "/" + split[1]);
                if (tempf.exists()) {
                    imagePath = temp[i] + "/" + split[1];
                }
            }
        }
        return imagePath;
    }
}
