package cn.ktc.jkf.serverapi.data.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cn.ktc.jkf.serverapi.data.IUserData;

/**
 * 处理后台返回的JSON的基类
 */
/*
 * { "code": 0, "message": "xxx", }
 */
public class BaseJsonBean implements IJsonBean, IUserData {

    public BaseJsonBean() {
    }

    @Expose
    @SerializedName(value = "code")
    protected Integer code;
    @Expose
    @SerializedName(value = "message")
    public String message;

    @Expose(serialize = false, deserialize = false)
    private Object userData;

    /**
     * 判定对象是否还未JSON反序列化
     *
     * @return 未反序列化返回true
     */
    public final boolean noInited() {
        return code == null;
    }

    public final int getCode() {
        return code == null ? -1 : code;
    }

    public boolean isSuccessful() {
        return code == null || code == 0;
    }

    public final String getMessage() {
        return message;
    }

    @Override
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
