package com.ktc.doorplate;

import android.app.Application;

import com.ktc.doorplate.utils.ToastUtils;

import cn.ktc.jkf.utils.EnvironmentInfo;

public class DoorPlateApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtils.init(this);
        EnvironmentInfo.init(this, "doorplate");
    }
}
