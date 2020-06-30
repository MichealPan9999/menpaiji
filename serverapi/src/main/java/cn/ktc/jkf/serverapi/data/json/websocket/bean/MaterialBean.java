package cn.ktc.jkf.serverapi.data.json.websocket.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.ktc.jkf.serverapi.data.json.IJsonBean;

/**
 * {
 *             "file_url": "/upload/material/2020-06-05/搜狗截图20年05月27日0907_1.jpg",
 *             "name": "搜狗截图20年05月27日0907_1",
 *             "index": 1,
 *             "interval": 3,
 *             "id": 9,
 *             "type": 0
 *           }
 */
public class MaterialBean implements IJsonBean {


    /**
     * 素材名称
     */
    @Expose
    @SerializedName(value = "id")
    private int meterialId;
    /**
     * 素材名称
     */
    @Expose
    @SerializedName(value = "name")
    private String name;

    /**
     * 素材类型
     * 0 图片
     * 1 视频
     */
    @Expose
    @SerializedName(value = "type")
    private int type;
    /**
     * 素材内容
     * 根据素材内容播放对应素材
     */
    @Expose
    @SerializedName(value = "file_url")
    private String fileUrl;

    /**
     * 在第几分屏处
     */
    @Expose
    @SerializedName(value = "index")
    private int index;
    /**
     * 播放间隔 单位秒
     */
    @Expose
    @SerializedName(value = "interval")
    private int interval;

    public int getMeterialId() {
        return meterialId;
    }

    public void setMeterialId(int meterialId) {
        this.meterialId = meterialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
