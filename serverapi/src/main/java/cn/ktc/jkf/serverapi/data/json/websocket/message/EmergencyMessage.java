package cn.ktc.jkf.serverapi.data.json.websocket.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.ktc.jkf.serverapi.data.json.websocket.MessageType;
import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;

public class EmergencyMessage extends WebSocketMessage {
    public EmergencyMessage() {
        super(MessageType.TV_EMERGENCY);
    }

    /**
     * 字体大小
     */
    @Expose
    @SerializedName("color")
    private String fontColor;

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * 字体是否加粗
     * true 表示加粗 textView .setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
     * false 表示不加粗 textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
     */
    @Expose
    @SerializedName("bold")
    private int fontBold = 0;

    /**
     * 显示位置
     * 0：上
     * 1：中
     * 2：下
     */
    @Expose
    @SerializedName("location")
    private int position;

    /**
     * 滚动速度
     * 像素点越大越快
     */
    @Expose
    @SerializedName("speed")
    private int speed;

    /**
     * 播放时长
     * 0 代表一直显示
     * 其他代表具体时长 单位秒
     */
    @Expose
    @SerializedName("interval_time")
    private long duration;

    public int getAlwayShow() {
        return alwayShow;
    }

    public void setAlwayShow(int alwayShow) {
        this.alwayShow = alwayShow;
    }

    /**
     * 是否长显示 0表示常显示，1表示根据时长显示
     */
    @Expose
    @SerializedName("is_show")
    private int alwayShow;

    public String getFontSize() {
        return fontColor;
    }

    public void setFontSize(String fontcolor) {
        this.fontColor = fontcolor;
    }

    public boolean isFontBold() {
        return fontBold == 1;
    }

    public void setFontBold(int fontBold) {
        this.fontBold = fontBold;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
