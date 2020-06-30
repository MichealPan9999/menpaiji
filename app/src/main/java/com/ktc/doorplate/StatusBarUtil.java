package com.ktc.doorplate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StatusBarUtil {
    private static boolean barIsHiden;

    private static final int DISABLE_EXPAND = 0x00010000;
    private static final int DISABLE_NOTIFICATION_ICONS = 0x00020000;
    private static final int DISABLE_NOTIFICATION_ALERTS = 0x00040000;
    private static final int DISABLE_NOTIFICATION_TICKER = 0x00080000;
    private static final int DISABLE_SYSTEM_INFO = 0x00100000;
    private static final int DISABLE_HOME = 0x00200000;
    private static final int DISABLE_BACK = 0x00400000;
    private static final int DISABLE_CLOCK = 0x00800000;
    private static final int DISABLE_RECENT = 0x01000000;
    private static final int DISABLE_SEARCH = 0x02000000;

    private static final int DISABLE_EXPAND_LOW = 0x00000001;
    private static final int DISABLE_NONE = 0x00000000;


    /**
     * 隐藏虚拟按键及状态栏
     */
    public static void banStatusBar(Context context) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion <= 16) {
            setStatusBarDisable(context, DISABLE_EXPAND_LOW);
        } else {
            setStatusBarDisable(context, DISABLE_EXPAND | DISABLE_NOTIFICATION_ICONS | DISABLE_NOTIFICATION_ALERTS | DISABLE_NOTIFICATION_TICKER
                    | DISABLE_SYSTEM_INFO | DISABLE_HOME | DISABLE_BACK | DISABLE_CLOCK | DISABLE_RECENT | DISABLE_SEARCH | 0x04000000 | 0x08000000);
        }
    }

    /**
     * 显示虚拟按键及状态栏
     */
    @SuppressLint("WrongConstant")
    public static void unBanStatusBar(Context context) {
        Object service = null;
        //service = APP.getInstance().getSystemService("statusbar");
        service = context.getSystemService("statusbar");
        try {
            @SuppressLint("PrivateApi") Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod("disable", int.class);
            expand.invoke(service, DISABLE_NONE);
            barIsHiden = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    private static void setStatusBarDisable(Context context, int disable_status) {
        Object service = null;
        //service = APP.getInstance().getSystemService("statusbar");
        service = context.getSystemService("statusbar");
        try {
            @SuppressLint("PrivateApi") Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod("disable", int.class);
            expand.invoke(service, disable_status);
            barIsHiden = true;
        } catch (Exception e) {
            unBanStatusBar(context);
            e.printStackTrace();
        }
    }


    public static boolean barIsHiden() {
        return barIsHiden;
    }

    private static String TAG = "hookWebView";
    public static void hookWebView() {
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                Log.i(TAG, "sProviderInstance isn't null");
                return;
            }

            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                Log.i(TAG, "Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);
            if (sdkInt < 26) {//低于Android O版本
                Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
                }
            } else {
                Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                chromiumMethodName.setAccessible(true);
                String chromiumMethodNameStr = (String) chromiumMethodName.get(null);
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create";
                }
                Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
                if (staticFactory != null) {
                    sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
                }
            }

            if (sProviderInstance != null) {
                field.set("sProviderInstance", sProviderInstance);
                Log.i(TAG, "Hook success!");
            } else {
                Log.i(TAG, "Hook failed!");
            }
        } catch (Throwable e) {
            Log.w(TAG, e);
        }
    }

}
