package com.sky.imsky.controller.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.sky.imsky.R;

/**
 * Created by Administrator on 2018/1/26 0026.
 */

public class VoiceCallActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_call);
    }
}
