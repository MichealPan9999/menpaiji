package cn.ktc.jkf.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.File;
import java.io.FileReader;
import java.util.UUID;

/**
 * 一些变量的管理，例如：deviceId, OS, OSVer等
 *
 * @author hq
 */
public class EnvironmentInfo {
    private static final String OS_NAME = "android";

    /**
     * deviceId。默认为 {@link #KTC_DEVICE_ID}
     * 如果 {@link #KTC_DEVICE_ID}为空，则为空
     * 注意：App初始化时需要检测是否为空，如果为空则需要设置
     */
    private static String DEVICE_ID = null;

    /**
     * 默认语言。空表示不加入到HTTP参数中
     */
    private static String LANGUAGE = "";

    /**
     * 定义当前的硬件类别。默认为tv
     * App上层应该通过
     */
    private static String HW_NAME = EHardWareValue.TV.getValue();

    /**
     * 判断是否是KTC的设备
     * 判定方法：获取系统属性值，包括ktc和horion则表示为ktc定义的设备
     * <p>
     * 注意：由于客制化原因，有部分设备判定为false，但实际上依旧是KTC生产的
     */
    private static final boolean IS_KTC_DEVICE;
    /**
     * KTC设备的序列号
     * 1.KTC设置：读取EMMC的CID
     * 2.其他设备：根据android_id生成。恢复出厂设置会被复位
     * <p>
     * 返回null表示未获取到
     */
    private static final String KTC_DEVICE_ID;

    /**
     * 当前是使用的UID
     * 登录验证通过后设置正确的值
     * 未登录（或注销）被设置为-1
     */
    private static long UID = -1;

    /**
     * 当前登录使用的UTOKEN
     */
    private static String UTOKEN = null;
    /**
     * 保存当前的ApplicationContext
     */
    private static Context APPLICATION_CONTEXT;

    /**
     * LoginApi.init()返回的reqid，在WebSocket时需要附带上去
     */
    private static String INIT_REQ_ID = "";

    /**
     * HTTP请求的服务器地址
     */
    private static String HTTP_SERVER;

    /**
     * URL中的app参数值，通知后台是哪个App请求的
     */
    private static String APP_NAME;

    public enum EHardWareValue {
        /**
         * Android TV
         */
        TV("tv"),
        /**
         * Android Phone
         */
        ANDROID_PHONE("aphone"),
        /**
         * iPhone
         */
        IOS_PHONE("iphone"),
        /**
         * iPad
         */
        IOS_PAD("ipad"),
        /**
         * MAC苹果电脑
         */
        MAC_PC("mac"),
        /**
         * Windows 电脑
         */
        WIN_PC("win"),

        REMOVE("");
        private final String value;

        EHardWareValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    static {
        boolean ktc = false;
        String[] props = new String[]{
                Build.HARDWARE,
                Build.MANUFACTURER,
                Build.BRAND,
                Build.MODEL,
        };
        for (String prop : props) {
            prop = prop.toLowerCase();
            if (prop.contains("ktc") || prop.contains("horion")) {
                ktc = true;
                break;
            }
        }
        IS_KTC_DEVICE = ktc;
        //读取mmc的cid作为序列号
        String deviceId = null;
        String[] files = new String[]{
                "/sys/class/mmc_host/mmc0/mmc0:0001/cid",
        };
        for (String file : files) {
            String id = readFileContent(file);
            if (!TextUtils.isEmpty(id)) {
                //读取到ID了，使用这个作为 DeviceId
                deviceId = DigestUtil.getSha1String(id);
                break;
            }
        }
        if (IS_KTC_DEVICE) {
            String mac = getEthMac();
            if (!TextUtils.isEmpty(mac)) {
                /*
                    找到了MAC地址，需要按照规则生成MAC
                 */
                String hash = DigestUtil.hashPassword(mac);
                deviceId = "KM" + deviceId.substring(0, 14) + hash.substring(0, 12) + mac;
            } else {
                //替换前面1个字符为K
                deviceId = "K" + deviceId.substring(1);
            }
        }
        KTC_DEVICE_ID = deviceId;
        DEVICE_ID = KTC_DEVICE_ID;
    }

    /**
     * 初始化。必须要调用一次
     *
     * @param context Context
     * @param appName 请求后台的的app字段名称，告知后台当前是哪个app请求的
     */
    public static void init(Context context, String appName) {
        APPLICATION_CONTEXT = context.getApplicationContext();
        APP_NAME = appName;
        if (!IS_KTC_DEVICE) {
            //需要重新设置deviceId。设置为android_id
            String androidId = Settings.System.getString(context.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            String deviceId = Build.ID + androidId;
            //进行HASH
            deviceId = DigestUtil.getSha1String(deviceId);
            DEVICE_ID = deviceId;
        }
    }

    public static Context getApplicationContext() {
        return APPLICATION_CONTEXT;
    }


    public static void setAppName(String appName) {
        APP_NAME = appName;
    }

    public static String getAppName() {
        return APP_NAME;
    }

    /**
     * 获取一个新的UUID
     *
     * @param rep 是否将获取到的分隔符-替换成指定的。null表示不替换
     */
    public static String newUUID(String rep) {
        String uuid = UUID.randomUUID().toString();
        if (rep != null) {
            uuid = uuid.replace("-", rep);
        }
        return uuid;
    }

    /**
     * 获取设备ID，默认下等于 KTC_DEVICE_ID
     * 如果需要更改（比如为空时），请在App中调用{@link #setDeviceId(String)}
     * 如果要获取KTC_DEVICEID（可能为空），请调用 {@link #getKtcDeviceId()}
     * 注意：KTC设备总是返回KTC_DEVICE_ID
     */
    public static String getDeviceId() {
        return DEVICE_ID;
    }

    /**
     * 上层App根据当前是硬件设备还是个人设置
     * 1.硬件设备：每次都是统一的
     * 2.个人注册：先随机生成，认证成功后保存到本地。以后如果失效才会重新随机生成
     */
    public static void setDeviceId(String deviceId) {
        DEVICE_ID = deviceId;
    }

    public static void setHttpServer(String host) {
        HTTP_SERVER = host;
    }

    /**
     * 获取HTTP请求的服务器地址
     *
     * @return http服务器地址
     */
    public static String getHttpServer() {
        return HTTP_SERVER;
    }

    /**
     * 获取 LoginApi.init() 接口用到的HTTP 服务器地址
     *
     * @return http服务器地址
     */
    public static String getInitHttpServer() {
        return HTTP_SERVER;
    }

    /**
     * 获取OS名称
     *
     * @return 总是返回 "android"
     */
    public static String getOSName() {
        return OS_NAME;
    }

    /**
     * 当前android的版本
     */
    public static String getOSVersion() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    /**
     * 获取当前的默认语言
     */
    public static String getLanguage() {
        return LANGUAGE;
    }

    /**
     * 设置当前的默认语言
     * 如果为空表示不加入到HTTP请求参数中
     */
    public static void setLanguage(String lan) {
        EnvironmentInfo.LANGUAGE = lan;
    }

    /**
     * 获取当前设备是否是KTC生成的设备
     *
     * @return 是否是KTC生产的设备
     */
    public static boolean isKtcDevice() {
        return IS_KTC_DEVICE;
    }

    /**
     * 设置硬件名称。所有硬件的名称在{@link EHardWareValue}中定义
     *
     * @param name 需要设置的硬件类别名称
     */
    public static void setHardware(EHardWareValue name) {
        HW_NAME = name.value;
    }

    /**
     * 获取当前硬件类别名称{@link EHardWareValue}
     *
     * @return 当前的硬件名称
     */
    public static String getHardware() {
        return HW_NAME;
    }

    /**
     * 获取KTC的设备号
     * 注意：可能为空
     *
     * @return 设备序列号
     */
    public static String getKtcDeviceId() {
        return KTC_DEVICE_ID;
    }

    public static void setUID(long uid) {
        EnvironmentInfo.UID = uid;
    }
    public static void setToken(String token)
    {
        EnvironmentInfo.UTOKEN = token;
    }

    public static long getUID() {
        return UID;
    }

    public static String getUToken() {
        return UTOKEN;
    }

    public static boolean isDebugOkhttp() {
        return Log.HTTP;
    }

    public static void setDebugOkhttp(boolean set) {
        Log.HTTP = set;
    }

    public static boolean isDebugWebSocket() {
        return Log.WSR;
    }

    public static void setDebugWebSocket(boolean set) {
        Log.WSR = set;
        Log.WSS = set;
    }

    public static void setInitReqId(String id) {
        INIT_REQ_ID = id;
    }

    public static String getInitReqId() {
        return INIT_REQ_ID;
    }

    /**
     * 读取指定文件中的内容
     *
     * @param fileName 文件全路径
     * @return 返回文件内容字符串。null表示读取失败
     */
    private static String readFileContent(String fileName) {

        FileReader localFileReader = null;
        try {
            File file = new File(fileName);
            //先判断，避免总是抛出异常
            if (!file.canRead()) {
                return null;
            }
            localFileReader = new FileReader(fileName);
            char[] buffer = new char[8192];
            int size = localFileReader.read(buffer, 0, buffer.length);
            localFileReader.close();
            if (size >= 0) {
                return new String(buffer, 0, size);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 获取以太网的MAC地址
     *
     * @return null表示获取失败
     */
    private static String getEthMac() {
        final String files[] = new String[]{
                "/sys/class/net/eth0/address",
        };
        for (String file : files) {
            String addr = readFileContent(file);
            if (!TextUtils.isEmpty(addr)) {
                //获取到了内容，
                String mac = addr.replace(":", "").toLowerCase();
                if (mac.length() >= 12) {
                    return mac.substring(0, 12);
                }
            }
        }
        return null;
    }
}
