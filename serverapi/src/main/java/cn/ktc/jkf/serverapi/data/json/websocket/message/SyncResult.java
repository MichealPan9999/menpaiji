package cn.ktc.jkf.serverapi.data.json.websocket.message;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.ktc.jkf.serverapi.data.json.BaseJsonBean;
import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;
import cn.ktc.jkf.serverapi.data.json.websocket.bean.ChannelInfo;

/**
 * 当需要Reply后台时，使用此消息回复
 *
 * @author panzq
 */

/**
 * {
 *     "ctrls": [
 *         {
 *             "version": 79,
 *             "msgtype": 1001005,
 *             "content": "zh-CN"
 *         },
 *         {
 *             "version": 80,
 *             "msgtype": 1001009,
 *             "content": "/upload/background_url/1592362014366.jpg"
 *         },
 *         {
 *             "version": 82,
 *             "msgtype": 1001010,
 *             "content": "1"
 *         },
 *         {
 *             "version": 83,
 *             "msgtype": 1001001,
 *             "content": null
 *         }
 *     ],
 *     "code": 0,
 *     "channels": [
 *         {
 *             "end_date": "00:00:00",
 *             "play_rule": 0,
 *             "week": "",
 *             "name": "pzq频道测试2",
 *             "rule": 0,
 *             "id": 6,
 *             "programs": [
 *                 {
 *                     "datetime": "50",
 *                     "proportion": "23",
 *                     "materials": [
 *                         {
 *                             "file_url": "/upload/material/1592267822050.jpg",
 *                             "name": "bbb.jpg",
 *                             "index": 1,
 *                             "interval": 2,
 *                             "id": 12,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267827228.jpg",
 *                             "name": "boot0.jpg",
 *                             "index": 1,
 *                             "interval": 2,
 *                             "id": 13,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267909108.mp4",
 *                             "name": "test.mp4",
 *                             "index": 1,
 *                             "interval": 2,
 *                             "id": 14,
 *                             "type": 1
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267914993.jpg",
 *                             "name": "Indian 1920x1080.jpg",
 *                             "index": 1,
 *                             "interval": 2,
 *                             "id": 15,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267920934.jpg",
 *                             "name": "img100.jpg",
 *                             "index": 1,
 *                             "interval": 2,
 *                             "id": 16,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267654583.jpg",
 *                             "name": "平板桌面.jpg",
 *                             "index": 2,
 *                             "interval": 3,
 *                             "id": 11,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267822050.jpg",
 *                             "name": "bbb.jpg",
 *                             "index": 2,
 *                             "interval": 3,
 *                             "id": 12,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267827228.jpg",
 *                             "name": "boot0.jpg",
 *                             "index": 2,
 *                             "interval": 3,
 *                             "id": 13,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267909108.mp4",
 *                             "name": "test.mp4",
 *                             "index": 2,
 *                             "interval": 3,
 *                             "id": 14,
 *                             "type": 1
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267914993.jpg",
 *                             "name": "Indian 1920x1080.jpg",
 *                             "index": 2,
 *                             "interval": 3,
 *                             "id": 15,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267920934.jpg",
 *                             "name": "img100.jpg",
 *                             "index": 2,
 *                             "interval": 3,
 *                             "id": 16,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267927403.jpg",
 *                             "name": "LockScreen___2560_1440_notdimmed.jpg",
 *                             "index": 2,
 *                             "interval": 3,
 *                             "id": 17,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267932211.jpg",
 *                             "name": "N6.jpg",
 *                             "index": 2,
 *                             "interval": 3,
 *                             "id": 18,
 *                             "type": 0
 *                         },
 *                         {
 *                             "file_url": "/upload/material/1592267937783.jpg",
 *                             "name": "pic2.jpg",
 *                             "index": 2,
 *                             "interval": 3,
 *                             "id": 19,
 *                             "type": 0
 *                         }
 *                     ],
 *                     "name": "pzq节目测试1",
 *                     "pattern": 1,
 *                     "id": 4
 *                 }
 *             ],
 *             "star_date": "16:11:00"
 *         }
 *     ],
 *     "ctrlversion": "83"
 * }
 */
public class SyncResult extends BaseJsonBean {


    @Expose
    @SerializedName(value = "version", alternate = "ctrlversion")
    private int version;

    @Expose
    @SerializedName("ctrls")
    private List<WebSocketMessage> ctrlMessages;

    @Expose
    @SerializedName("channels")
    private List<ChannelInfo> channelMessages;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<WebSocketMessage> getCtrlMessages() {
        return ctrlMessages;
    }

    public void setCtrlMessages(List<WebSocketMessage> ctrlMessages) {
        this.ctrlMessages = ctrlMessages;
    }
    public List<ChannelInfo> getChannelMessages() {
        return channelMessages;
    }

    public void setChannelMessages(List<ChannelInfo> channelMessages) {
        this.channelMessages = channelMessages;
    }
}
