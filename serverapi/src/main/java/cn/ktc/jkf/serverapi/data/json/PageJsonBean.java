package cn.ktc.jkf.serverapi.data.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 分页的数据
 */
public class PageJsonBean extends BaseJsonBean {
    /**
     * 本次请求的开始序号，用于列表太长多页请求
     */
    @Expose
    @SerializedName(value = "start")
    protected int start;

    /**
     * 下次请求时的start数值，用于多页请求
     * -1表示已经到末尾，无需请求后面数据
     */
    @Expose
    @SerializedName(value = "next")
    protected int next;

    public int getStart() {
        return start;
    }

    public int getNext() {
        return next;
    }

    public boolean isFinished() {
        return next == -1;
    }
}
