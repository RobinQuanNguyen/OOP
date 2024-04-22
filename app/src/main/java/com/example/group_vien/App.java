package com.example.group_vien;

import android.app.Application;

import com.tencent.mmkv.MMKV;

import dagger.hilt.android.HiltAndroidApp;
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MMKV.initialize(this);
    }
}
