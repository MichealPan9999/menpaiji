package com.ktc.doorplate.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.ktc.doorplate.BuildConfig;

import cn.ktc.jkf.utils.RxHelper;

public class ToastUtils {
    private static Toast toast;
    private static Context context;

    public static void init(Context context) {
        ToastUtils.context = context.getApplicationContext();
    }

    public static synchronized void debugShow(String content) {
        if (BuildConfig.DEBUG) {
            showToast(content);
        }
    }

    public static synchronized void showToast(int id) {
        showToast(context.getString(id));
    }

    public static synchronized void showToast(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        RxHelper.excuteMainThread((p) -> {
            cancel();
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
            toast.show();
        }, content);
    }

    public static synchronized void cancel() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}
