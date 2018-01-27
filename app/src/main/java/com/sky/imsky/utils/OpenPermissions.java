package com.sky.imsky.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2018/1/23 0023.
 */

public class OpenPermissions {

    private static OpenPermissions instance = new OpenPermissions();
    private Activity mActivity;

    private OpenPermissions() {
    }

    public static OpenPermissions getInstance() {
        return instance;
    }

    public void init(Activity activity) {
        this.mActivity = activity;

        getWrite();
        getCamera();
        getRecordAudio();
    }

    private void getCamera() {
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    private void getWrite() {
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }
    }

    private void getRecordAudio() {
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
    }

}
