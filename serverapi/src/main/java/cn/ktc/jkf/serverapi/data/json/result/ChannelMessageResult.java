package cn.ktc.jkf.serverapi.data.json.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.ktc.jkf.serverapi.data.json.BaseJsonBean;
import cn.ktc.jkf.serverapi.data.json.websocket.message.ChannelInfoMessage;

/**
 *  "channelmessage": {
 *     "version": 8,
 *     "channels": [
 *       {
 *         "channelnum": 5,
 *         "name": "xxxxxx",
 *         "starttime": 12345678,
 *         "endtime": 23456789,
 *         "playtype": 0,
 *         "repeattype": 0,
 *         "programs": [
 *           {
 *             "name": "xxxxxx",
 *             "interval": 1234,
 *             "meterial": [
 *               {
 *                 "name": "xxxxxx",
 *                 "type": 0,
 *                 "content": "xxxxxx"
 *               },
 *               {
 *                 "name": "xxxxxx",
 *                 "type": 0,
 *                 "content": "xxxxxx"
 *               }
 *             ]
 *           },
 *           {
 *             "name": "xxxxxx",
 *             "interval": 1234,
 *             "meterial": [
 *               {
 *                 "name": "xxxxxx",
 *                 "type": 0,
 *                 "content": "xxxxxx"
 *               },
 *               {
 *                 "name": "xxxxxx",
 *                 "type": 0,
 *                 "content": "xxxxxx"
 *               }
 *             ]
 *           }
 *         ],
 *         "repeatdate": [
 *           0,
 *           1,
 *           2,
 *           3
 *         ]
 *       }
 *     ]
 *   }
 */
public class ChannelMessageResult extends BaseJsonBean {

    /**
     * 频道消息内容
     */
    @Expose
    @SerializedName(value = "channels", alternate = "channellist")
    private List<ChannelInfoMessage> channels;

    public List<ChannelInfoMessage> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelInfoMessage> channels) {
        this.channels = channels;
    }
}
