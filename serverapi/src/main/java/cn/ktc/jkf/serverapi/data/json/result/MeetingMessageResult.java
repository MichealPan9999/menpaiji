package cn.ktc.jkf.serverapi.data.json.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.ktc.jkf.serverapi.data.json.BaseJsonBean;
import cn.ktc.jkf.serverapi.data.json.websocket.bean.MeetingInfo;

/**
 * "meetingmessage": {
 *     "version": 8,
 *     "meetings": [
 *       {
 *         "meetingnum": 1,
 *         "topic": "评审会",
 *         "appointment": "xxxxxx",
 *         "starttime": 12345678,
 *         "endtime": 23456789
 *       },
 *       {
 *         "meetingnum": 1,
 *         "topic": "周例会",
 *         "appointment": "xxxxxx",
 *         "starttime": 12345678,
 *         "endtime": 23456789
 *       }
 *     ]
 *   },
 */
public class MeetingMessageResult extends BaseJsonBean {

    /**
     * 设备控制指令的最新版本号
     */
    @Expose
    @SerializedName(value = "version", alternate = "Version")
    private String version;

    /**
     * 设备控制指令的最新版本号
     */
    @Expose
    @SerializedName(value = "meetings", alternate = "meetinglist")
    private List<MeetingInfo> meetings;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<MeetingInfo> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<MeetingInfo> meetings) {
        this.meetings = meetings;
    }
}
