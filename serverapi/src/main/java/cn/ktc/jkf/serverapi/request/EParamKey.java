package cn.ktc.jkf.serverapi.request;

public enum EParamKey {
    /**
     * 哪种硬件上
     */
    HW("hw"),
    /**
     * 当前操作系统名称
     */
    OS("os"),
    /**
     * 当前操作系统版本
     */
    OSVER("osver"),
    /**
     * 当前的语言
     */
    LANGUAGE("lan"),
    /**
     * 请求ID。用户登录后可以忽略
     */
    REQID("reqid"),
    /**
     * 当前登录的用户ID
     */
    UID("uid"),
    /**
     * 当前登录的用户TOKEN，后台用于验证用户有效性
     */
    UTOKEN("utoken"),
    /**
     * 当前的设备ID
     */
    DEVICEID("deviceid"),
    /**
     * 通知后台是哪个App请求的
     */
    APPNAME("app"),

    DUMMY("");
    private final String name;

    EParamKey(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}