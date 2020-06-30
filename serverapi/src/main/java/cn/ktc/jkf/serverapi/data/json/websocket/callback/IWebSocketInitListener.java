package cn.ktc.jkf.serverapi.data.json.websocket.callback;

import java.util.ArrayList;

import cn.ktc.jkf.serverapi.data.json.websocket.SendMessageWrapper;

public interface IWebSocketInitListener {

    void onWebSocketInit();

    void onWebSocketRetry(ArrayList<SendMessageWrapper> list);
}
