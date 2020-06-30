package cn.ktc.jkf.serverapi.data.json.websocket.bean;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

import cn.ktc.jkf.serverapi.data.json.IJsonBean;
import cn.ktc.jkf.utils.TextUtils;

/**
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
 */
public class ProgramBean implements IJsonBean {

    /**
     * 节目名称
     */
    @Expose
    @SerializedName(value = "name")
    private String name;

    /**
     * 播放时间
     */
    @Expose
    @SerializedName(value = "datetime")
    private String playTime;

    /**
     * 素材内容
     */
    @Expose
    @SerializedName(value = "materials")
    private List<MaterialBean> materialBeans;
    /**
     * 素材内容
     */
    @Expose
    @SerializedName(value = "id")
    private int programId;

    /**
     * 模式（几分屏）分屏个数
     */
    @Expose
    @SerializedName(value = "pattern")
    private int pattern;

    /**
     * 各个分屏的比例
     */
    @Expose
    @SerializedName(value = "proportion")
    private String proportion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MaterialBean> getMaterialBeans() {
        return materialBeans;
    }

    public void setMaterialBeans(List<MaterialBean> materialBeans) {
        this.materialBeans = materialBeans;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public int getPattern() {
        return pattern;
    }

    public void setPattern(int pattern) {
        this.pattern = pattern;
    }

    public long getPlayTime() {
        Log.d("panzq", "getpalyTime = " + playTime);
        if (playTime == null) {
            return 0;
        }
        return Long.parseLong(playTime);
    }

    public void setDataTime(String playTime) {
        this.playTime = playTime;
    }

    /* public String getProportion() {
        return proportion;
    }*/

    public List<Integer> getProportions() {
        List<Integer> integers = new LinkedList<>();
        if (!TextUtils.isEmpty(proportion)) {
            String[] proportions = proportion.split(",");
            for (String p : proportions) {
                integers.add(Integer.parseInt(p));
            }
        }
        return integers;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }
}
