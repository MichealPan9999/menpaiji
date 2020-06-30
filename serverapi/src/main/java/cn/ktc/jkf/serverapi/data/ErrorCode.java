package cn.ktc.jkf.serverapi.data;

/**
 * 后台返回错误码定义
 *
 * @author hq
 */
public class ErrorCode {
    /**
     * 一般性的失败，比如充值密码时，无特殊错误码返回
     */
    public static final int ERROR = -1;
    /**
     * 返回正确
     */
    public static final int SUCCESS = 0;
    /**
     * 数据添加出现异常
     */
    public static final int ADD_ERROR = 100;
    /**
     * 编辑数据异常
     */
    public static final int EDIT_ERROR = 101;
    /**
     * 删除数据异常
     */
    public static final int DELETE_ERROR = 102;
    /**
     * 服务器未激活
     */
    public static final int SERVEL_NOACTIVATION = 40001;
    /**
     * 服务器已经激活
     */
    public static final int SERVEL_USEACTIVATION = 40002;
    /**
     * 服务器激活失败
     */
    public static final int ACTIVATION_FAILED = 40003;
    /**
     * 激活服务器管理员账号添加失败
     */
    public static final int ADD_ACCOUNT_FAILED = 40004;
    /**
     * 激活服务器创建签名失败
     */
    public static final int CREATE_SIGN_FAILED = 40005;
    /**
     * 未登录
     */
    public static final int NOLOGIN = 40006;
    /**
     * 账号密码不匹配
     */
    public static final int ACCOUNT_PWD_ERROR = 40007;
    /**
     * 登录更新数据异常
     */
    public static final int LOGIN_FAILED = 40008;
    /**
     * 设备已绑定
     */
    public static final int DEVICE_USE_BIND = 40009;
    /**
     * 设备不存在
     */
    public static final int DEVICE_NONENTITY = 400010;
    /**
     * 设备绑定失败
     */
    public static final int DEVICE_BIND_FAILED = 40011;
    /**
     * 文件格式错误
     */
    public static final int FILE_EXT_ERROR = 40012;
    /**
     * 素材不存在
     */
    public static final int MATERIAL_NONENTITY = 40013;

    /**
     * 请求时UTOKN无效（包括登录与后续请求） UID无效也会返回这个错误（因为找不到与UID对应的UTOKEN）
     */
    public static final int UTOKEN_INVALID = 100010001;
}
