package cn.ktc.jkf.serverapi.data.json.websocket.bean;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import cn.ktc.jkf.serverapi.data.json.IJsonBean;
import cn.ktc.jkf.utils.TextUtils;

/**
 * {
 * "end_date": "09:59:59",
 * "play_rule": 1,
 * "week": "1,2,3,4",
 * "name": "我是频道标题",
 * "rule": 0,
 * "id": 4,
 * "programs": [
 * {
 * "materials": [
 * {
 * "file_url": "/upload/material/2020-06-05/搜狗截图20年05月27日0907_1.jpg",
 * "name": "搜狗截图20年05月27日0907_1",
 * "index": 1,
 * "interval": 3,
 * "id": 9,
 * "type": 0
 * },
 * {
 * "file_url": "/upload/material/2020-06-05/搜狗截图20年05月27日0907_11.jpg",
 * "name": "搜狗截图20年05月27日0907_11",
 * "index": 1,
 * "interval": 3,
 * "id": 10,
 * "type": 0
 * },
 * {
 * "file_url": "/upload/material/2020-06-05/搜狗截图20年05月27日0907_1.jpg",
 * "name": "搜狗截图20年05月27日0907_1",
 * "index": 2,
 * "interval": 3,
 * "id": 9,
 * "type": 0
 * },
 * {
 * "file_url": "/upload/material/2020-06-05/搜狗截图20年05月27日0907_11.jpg",
 * "name": "搜狗截图20年05月27日0907_11",
 * "index": 2,
 * "interval": 3,
 * "id": 10,
 * "type": 0
 * }
 * ],
 * "name": "我是一个节目",
 * "pattern": 2,
 * "id": 3
 * }
 * ],
 * "star_date": "08:00:00"
 * }
 */
public class ChannelInfo implements IJsonBean {
    @Expose
    @SerializedName("star_date")
    private String startTime;

    @Expose
    @SerializedName("end_date")
    private String endTime;


    /**
     * 播放规则，0自动1手动
     */
    @Expose
    @SerializedName("play_rule")
    private int playRule;
    /**
     * 自定义星期几播放[1-7]，之前用,号隔开
     */
    @Expose
    @SerializedName("week")
    private String weeks;
    //private List<Integer> weeks;
    @Expose
    @SerializedName("name")
    private String title;
    @Expose
    @SerializedName("id")
    private int ChannelId;
    /**
     * 重复规则：0一次，1每天，2自定义
     */
    @Expose
    @SerializedName("rule")
    private int rule;
    @Expose
    @SerializedName("programs")
    private List<ProgramBean> programs;

    public String getStartTimeStr() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPlayRule() {
        return playRule;
    }

    public void setPlayRule(int playRule) {
        this.playRule = playRule;
    }

    public List<Integer> getWeeks() {
        List<Integer> weekList = null;
        if (!TextUtils.isEmpty(weeks)) {
            weekList = new ArrayList<>();
            String week[] = weeks.split(",");
            for (String w : week) {
                weekList.add(Integer.parseInt(w));
            }
        }
        return weekList;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getChannelId() {
        return ChannelId;
    }

    public void setChannelId(int channelId) {
        ChannelId = channelId;
    }

    public int getRule() {
        return rule;
    }

    public void setRule(int rule) {
        this.rule = rule;
    }

    public List<ProgramBean> getPrograms() {
        return programs;
    }

    public void setPrograms(List<ProgramBean> programs) {
        this.programs = programs;
    }
}
