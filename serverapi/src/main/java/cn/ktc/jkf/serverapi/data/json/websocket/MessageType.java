package cn.ktc.jkf.serverapi.data.json.websocket;

import java.lang.reflect.Field;

/**
 * 定义WebSocket用到的消息类型
 * <p>
 * 增加消息类型，需要做如下操作 1.增加对应的JavaBean类，类似
 * {@link cn.ktc.jkf.serverapi.data.json.websocket.message.EmptyMessage} 2.在
 * {@link WebSocketMessage#fromJson(String)} 方法中增加返回对应的JavaBean类
 *
 * @author hq
 */
public final class MessageType {

    /**
     * 空数据包，不用做任何处理
     */
    public static final int NULL_DATA = 0;
    /**
     * 心跳数据。用于长连接时避免超时返回的心跳数据
     */
    public static final int HEARTBEAT = 1;
    /**
     * 建立web Socket长连接后，App会立即发送这个消息给后台，告知后台一些基本参数
     */
    public static final int APP_INIT = 2;
    /**
     * 通知指令消息，后台有指令更改或创建，通知TV端。
     */
    public static final int SERVER_NOTIFY = 1000000;
    /**
     * 锁屏指令
     */
    public static final int TV_LOCK_SCREEN = 1001001;
    /**
     * tv定时开关机指令,每天到了时间执行开关机操作
     */
    public static final int TV_POWER_ON_OFF_TIMING = 1001002;
    /**
     * tv定时开关机指令
     */
    public static final int TV_BOOT_LOGO = 1001003;
    /**
     * tv更改开机信源指令
     */
    public static final int TV_BOOT_SOURCE = 1001004;
    /**
     * tv切换语言
     */
    public static final int TV_SWITCH_LANGUAGE = 1001005;
    /**
     * tv切换语言
     */
    public static final int TV_TOUCH_STATE = 1001006;

    /**
     * tv安装apk
     */
    public static final int TV_INSTALL_APK = 1001007;
    /**
     * tv系统升级
     */
    public static final int TV_UPGRADE_SYSTEM = 1001008;
    /**
     * 更换背景
     */
    public static final int TV_CHANGE_BACKGROUND = 1001009;
    /**
     * tv即时开关机，区分定时开关机
     */
    public static final int TV_POWER_ON_OFF_IMMEDIATE = 1001010;

    /**
     * tv端收到紧急消息
     */
    public static final int  TV_EMERGENCY= 2001001;

    /**
     * tv端收到频道信息
     */
    public static final int  TV_CHANNELINFO= 3001001;
    /**
     * tv端收到结束播放频道信息
     */
    public static final int  TV_CHANNELINFO_STOP= 3001002;
    /**
     * 仅仅调试用，根据数值获取到字段名称 注意：如果在Release模式下，使用Proguard则获取到的名称是混淆后的，无参考意义
     *
     * @param type type数值
     * @return 字段名称。
     */
    public static String getDebugName(int type) {
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            return "";
//        }
        Field[] fields = MessageType.class.getFields();
        for (Field field : fields) {
            try {
                int v = field.getInt(null);
                if (v == type) {
                    return field.getName();
                }
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

}
