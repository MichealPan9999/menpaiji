package cn.ktc.jkf.serverapi.utils;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;

import java.util.Locale;

public class SystemUtil {

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return SystemProperties.get("ro.product.version", Build.UNKNOWN);
    }

    /**
     * 获取手机型号
     *
     * @return 皓丽tv型号
     */
    public static String getSystemModel() {
        return SystemProperties.get("ro.product.ota.model", Build.UNKNOWN);
    }

    /**
     * 获取触摸状态
     *
     * @return false表示打开状态，true表示关闭状态
     */
    public static boolean getTouchState() {
        return SystemProperties.getBoolean("persist.sys.ktc.touch_lock", false);
    }

    /**
     * 获取Locale，通过Locale可以获取到国家信息及系统语言
     *
     * @return
     */
    public static Locale getLocale() {
        String localeStr = SystemProperties.get("persist.sys.locale", "en");
        Locale locale;
        if (localeStr.contains("-")) {
            locale = new Locale(localeStr.split("-")[0], localeStr.split("-")[1]);
        } else if (localeStr.equals("en")) {
            locale = Locale.ENGLISH;
        } else if (localeStr.equals("ko")) {
            locale = Locale.KOREAN;
        } else if (localeStr.equals("ja")) {
            locale = Locale.JAPANESE;
        } else {
            locale = Locale.CHINESE;
        }
        return locale;
    }

    /**
     * 设置背景图
     * 每次执行设置背景图时，将对应的背景图内容设置进去
     * @param background
     */
    public static void setBackGround(String background){
        SystemProperties.set("persist.sys.background",background);
    }

    /**
     * 获取背景图
     * @return 默认值未default
     */
    public static String getBackGround(){
        return SystemProperties.get("persist.sys.background","default");
    }

    /**
     * 设置开机logo
     * 每次执行设置开机logo时，将对应的开机logo内容设置进去
     * @param logo
     */
    public static void setBootLogo(String logo){
        SystemProperties.set("persist.sys.boot.logo",logo);
    }

    /**
     * 获取开机logo
     * @return 默认值未default
     */
    public static String getBootLogo(){
        return SystemProperties.get("persist.sys.boot.logo","default");
    }
    /**
     * 获取开机信源的值
     * @return 默认值未default
     */
    public static String getBootSource() {
        return SystemProperties.get("persist.sys.bootsource", "usb");
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
}
