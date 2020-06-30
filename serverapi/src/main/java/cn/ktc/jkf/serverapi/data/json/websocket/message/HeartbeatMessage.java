package cn.ktc.jkf.serverapi.data.json.websocket.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cn.ktc.jkf.serverapi.data.json.websocket.MessageType;
import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;
import cn.ktc.jkf.utils.EnvironmentInfo;

/**
 * 心跳数据包
 * <p>
 * {@link cn.ktc.jkf.serverapi.data.json.websocket.MessageType#HEARTBEAT}
 * <p>
 * 20190929: 增加 "meetingid" 字段，如果已经加入会议室，此处为当前会议室的ID 取代以前的 会议室HTTP心跳
 *
 * @author hq
 */
public class HeartbeatMessage extends WebSocketMessage {
    /**
     * 所在会议室的ID<br>
     * 如果不在会议室则为null
     */
    @Expose
    @SerializedName("meetingId")
    private Integer meetingId;

    @Expose
    @SerializedName("memberId")
    private Integer memberId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * [可选]仅仅在TV端使用到
     */
    @Expose
    @SerializedName("deviceid")
    private String deviceId;

    public HeartbeatMessage() {
        super(MessageType.HEARTBEAT);
        this.setDeviceId(EnvironmentInfo.getDeviceId());
    }

    /**
     * 如果当前正在会议中，请调用此方法设置
     * 
     * @param meetingId 会议室ID
     * @param memberId  所在会议室的MemberID
     */
    public HeartbeatMessage setMeetingId(Integer meetingId, Integer memberId) {
        this.memberId = memberId;
        this.meetingId = meetingId;
        return this;
    }

    /**
     * 注意：可能会返回null
     */
    public Integer getMeetingId() {
        return this.meetingId;
    }

    public Integer getMemberId() {
        return this.memberId;
    }
}
