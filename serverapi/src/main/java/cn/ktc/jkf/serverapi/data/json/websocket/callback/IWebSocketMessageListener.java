package cn.ktc.jkf.serverapi.data.json.websocket.callback;

import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;

/**
 * WebSocket 接收到后台消息的监听
 * WebSocket接收到后台消息后，回调到App（假如App注册了）
 * 注意：主要用于后台主动发消息，ServerApi接收到后回调给App
 *
 * @author hq
 */
public interface IWebSocketMessageListener {
    /**
     * WebSocket 消息回调
     *
     * @param message 消息
     * @return !=null将会通过WebSocket发送回后台
     */
    WebSocketMessage onMessageReceived(WebSocketMessage message);
}
