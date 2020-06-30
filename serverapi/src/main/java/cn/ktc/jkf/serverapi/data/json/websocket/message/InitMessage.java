package cn.ktc.jkf.serverapi.data.json.websocket.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cn.ktc.jkf.serverapi.data.json.websocket.MessageType;
import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;
import cn.ktc.jkf.utils.EnvironmentInfo;

/**
 * WebSocket连接成功后，需要发送DeviceID等消息通知到后台
 * <p>
 * {@link cn.ktc.jkf.serverapi.data.json.websocket.MessageType#APP_INIT}
 *
 * @author hq
 */
public class InitMessage extends WebSocketMessage {
    public InitMessage() {
        super(MessageType.APP_INIT);
        this.setDeviceId(EnvironmentInfo.getDeviceId());
        this.setReqId(EnvironmentInfo.getInitReqId());
    }

    @Expose
    @SerializedName("lan")
    private String lan;
    @Expose
    @SerializedName("hw")
    private String hw;
    @Expose
    @SerializedName("os")
    private String os;
    @Expose
    @SerializedName(value = "osver")
    private String osVer;

    /**
     * [可选]仅仅在TV端使用到
     */
    @Expose
    @SerializedName("deviceid")
    private String deviceId;
    /**
     * [可选]如果是注册用户，则保存实际的注册用户的ID
     */
    @Expose
    @SerializedName("uid")
    private Integer uid;

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public String getHw() {
        return hw;
    }

    public void setHw(String hw) {
        this.hw = hw;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVer() {
        return osVer;
    }

    public void setOsVer(String osVer) {
        this.osVer = osVer;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getUid() {
        return uid == null ? 0 : uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

}
