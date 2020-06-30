package cn.ktc.jkf.serverapi.data.json.websocket.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cn.ktc.jkf.serverapi.data.json.BaseJsonBean;

/**
 * {
 * "meetingnum":1,
 * "version": 3,
 * "topic": "评审会",
 * "appointment":"xxxxxx",
 * "starttime": 12345678,
 * "endtime":23456789
 * }
 */
public class MeetingInfo extends BaseJsonBean {

    /**
     * 预约会议的会议号
     */
    @Expose
    @SerializedName(value = "meetingnum", alternate = "num")
    private int meetingnum;
    /**
     * 预约会议消息的版本号
     */
    @Expose
    @SerializedName(value = "version", alternate = "Verison")
    private int version;
    /**
     * 会议主题
     */
    @Expose
    @SerializedName("topic")
    private String topic;
    /**
     * 预约人
     */
    @Expose
    @SerializedName("appointment")
    private String appointment;

    /**
     * 开始时间
     */
    @Expose
    @SerializedName("starttime")
    private long starttime;

    /**
     * 结束时间
     */
    @Expose
    @SerializedName("endtime")
    private long endtime;

    public int getMeetingnum() {
        return meetingnum;
    }

    public void setMeetingnum(int meetingnum) {
        this.meetingnum = meetingnum;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAppointment() {
        return appointment;
    }

    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }
}
