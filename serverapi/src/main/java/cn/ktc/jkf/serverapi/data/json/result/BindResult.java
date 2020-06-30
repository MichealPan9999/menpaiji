package cn.ktc.jkf.serverapi.data.json.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cn.ktc.jkf.serverapi.data.ErrorCode;
import cn.ktc.jkf.serverapi.data.json.BaseJsonBean;

public class BindResult extends BaseJsonBean {
    /**
     * websocket 地址，调用init接口成功后，开始连接ws，如果手机端绑定失败，或者退出应用都要将ws断开。
     */
    @Expose
    @SerializedName(value = "wsaddr")
    private String wsAddr;

    public String getWsAddr() {
        return wsAddr;
    }

    public void setWsAddr(String wsAddr) {
        this.wsAddr = wsAddr;
    }

    /**
     * 设备是否已绑定,code与 {@link ErrorCode#DEVICE_USE_BIND}值对比，相等说明已经绑定过，否则没有被绑定过。
     *
     * @return
     */
    public boolean hasBinded() {
        int code = getCode();
        return code == ErrorCode.DEVICE_USE_BIND;
    }
}
