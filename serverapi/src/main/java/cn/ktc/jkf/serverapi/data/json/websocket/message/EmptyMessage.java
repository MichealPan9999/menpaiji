package cn.ktc.jkf.serverapi.data.json.websocket.message;

import cn.ktc.jkf.serverapi.data.json.websocket.MessageType;
import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;

/**
 * 空数据包，什么也不干
 * <p>
 * {@link cn.ktc.jkf.serverapi.data.json.websocket.MessageType#NULL_DATA}
 *
 * @author hq
 */
public class EmptyMessage extends WebSocketMessage {
    public EmptyMessage() {
        super(MessageType.NULL_DATA);
    }
}
