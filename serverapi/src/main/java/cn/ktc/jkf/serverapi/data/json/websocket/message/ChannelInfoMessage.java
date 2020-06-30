package cn.ktc.jkf.serverapi.data.json.websocket.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import cn.ktc.jkf.serverapi.data.json.websocket.MessageType;
import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;
import cn.ktc.jkf.serverapi.data.json.websocket.bean.ChannelInfo;

/**
 * {
 *   "channel": {
 *     "end_date": "09:59:59",
 *     "play_rule": 1,
 *     "week": "1",
 *     "name": "我是频道标题",
 *     "rule": 0,
 *     "id": 4,
 *     "programs": [
 *       {
 *         "materials": [
 *           {
 *             "file_url": "/upload/material/2020-06-05/搜狗截图20年05月27日0907_1.jpg",
 *             "name": "搜狗截图20年05月27日0907_1",
 *             "index": 1,
 *             "interval": 3,
 *             "id": 9,
 *             "type": 0
 *           },
 *           {
 *             "file_url": "/upload/material/2020-06-05/搜狗截图20年05月27日0907_11.jpg",
 *             "name": "搜狗截图20年05月27日0907_11",
 *             "index": 1,
 *             "interval": 3,
 *             "id": 10,
 *             "type": 0
 *           },
 *           {
 *             "file_url": "/upload/material/2020-06-05/搜狗截图20年05月27日0907_1.jpg",
 *             "name": "搜狗截图20年05月27日0907_1",
 *             "index": 2,
 *             "interval": 3,
 *             "id": 9,
 *             "type": 0
 *           },
 *           {
 *             "file_url": "/upload/material/2020-06-05/搜狗截图20年05月27日0907_11.jpg",
 *             "name": "搜狗截图20年05月27日0907_11",
 *             "index": 2,
 *             "interval": 3,
 *             "id": 10,
 *             "type": 0
 *           }
 *         ],
 *         "name": "我是一个节目",
 *         "pattern": 2,
 *         "id": 3
 *       }
 *     ],
 *     "star_date": "08:00:00"
 *   },
 *   "msgtype": 3001001
 * }
 */
public class ChannelInfoMessage extends WebSocketMessage {
    public ChannelInfoMessage() {
        super(MessageType.TV_CHANNELINFO);
    }

    /**
     * 频道信息
     */
    @Expose
    @SerializedName("channel")
    private ChannelInfo channelInfo;

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }
}
