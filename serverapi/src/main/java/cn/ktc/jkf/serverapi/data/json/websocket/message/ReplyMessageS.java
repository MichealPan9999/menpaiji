package cn.ktc.jkf.serverapi.data.json.websocket.message;

import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;

/**
 * 当需要Reply后台时，使用此消息回复
 *
 * @author hq
 */
public class ReplyMessageS extends WebSocketMessage {
    protected ReplyMessageS() {

    }

    /**
     * 获取一个空的回应消息
     * 空的回应消息仅仅是一个回复，用来告知后台
     *
     * @param msgId 针对这个ID的回复
     * @return ReplyMessageS 对象
     */
    public static ReplyMessageS getEmptyReplyMessage(int msgId) {
        ReplyMessageS message = new ReplyMessageS();
        message.setReplyId(msgId);
        return message;
    }
}
