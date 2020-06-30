package cn.ktc.jkf.serverapi.websocket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import cn.ktc.jkf.serverapi.data.json.websocket.IMessageSend;
import cn.ktc.jkf.serverapi.data.json.websocket.SendMessageWrapper;
import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;
import cn.ktc.jkf.serverapi.data.json.websocket.callback.IWebSocketMessageListener;
import cn.ktc.jkf.serverapi.data.json.websocket.callback.IWebSocketInitListener;
import cn.ktc.jkf.serverapi.data.json.websocket.callback.IWebSocketStateListener;

/**
 * WebSocket 对App的接口的封装
 *
 * @author zh
 */
public class WebSocketApi {
    private static String TAG = WebSocketApi.class.getSimpleName();
    private static WebSocketApi instance;
    private WebSocketClient mWebSocketClient;
    private Context mContext;

    private WebSocketApi() {
        mWebSocketClient = new WebSocketClient();
    }

    public static WebSocketApi getInstance() {
        if (instance == null) {
            synchronized (WebSocketApi.class) {
                if (instance == null) {
                    instance = new WebSocketApi();
                }
            }
        }
        return instance;
    }

    public WebSocketApi setInfo(String wsaddr) {
        mWebSocketClient.setInfo(wsaddr);
        return this;
    }

    /**
     * 将消息回调增加到Manager统一管理
     *
     * @param msgType    消息类型
     * @param listener   监听器
     * @param mainThread 是否在主线程中回调
     * @param group      分组名称
     * @return WebSocketApi对象，便于链式调用
     */
    public WebSocketApi addReceivedistener(int msgType, IWebSocketMessageListener listener, boolean mainThread, String group) {
        mWebSocketClient.addMessageReceivedListener(msgType, listener, mainThread, group);
        return this;
    }

    /**
     * 将消息回调增加到Manager统一管理
     * 默认为在主线程中回调
     *
     * @param msgType  消息类型
     * @param listener 监听器
     * @param group    分组名称
     * @return WebSocketApi对象，便于链式调用
     */
    public WebSocketApi addReceivedistener(int msgType, IWebSocketMessageListener listener, String group) {
        return addReceivedistener(msgType, listener, true, group);
    }

    /**
     * 移除接收消息监听器
     *
     * @param msgType  消息类型。0表示忽略，只需要比对 listener
     * @param listener 监听器
     */
    public WebSocketApi removeReceivedListener(int msgType, IWebSocketMessageListener listener) {
        mWebSocketClient.removeMessageReceivedListener(msgType, listener);
        return this;
    }

    public WebSocketApi removeReceivedListener(String group) {
        mWebSocketClient.removeMessageReceivedListener(group);
        return this;
    }

    /**
     * 增加WebSocket状态变更消息回调
     *
     * @param listener 监听器
     */
    public void addWebSocketStateChangeListener(IWebSocketStateListener listener, String group) {
        mWebSocketClient.addStateListener(listener, group);
    }

    /**
     * 移除WebSocket状态变更消息回调
     *
     * @param listener 需要移除的监听器
     */
    public void removeWebSocketStateChangeListener(IWebSocketStateListener listener) {
        mWebSocketClient.removeStateListener(listener);
    }

    /**
     * 增加WebSocket init消息回调
     *
     * @param listener 监听器
     */
    public void addWebSocketInitListener(IWebSocketInitListener listener, String group) {
        mWebSocketClient.addInitListener(listener, group);
    }

    /**
     * 移除WebSocket init消息回调
     *
     * @param listener 需要移除的监听器
     */
    public void removeWebSocketInitListener(IWebSocketInitListener listener) {
        mWebSocketClient.removeInitListener(listener);
    }

    /**
     * 对一个将要发送的消息封装，这样在App端可以方便的设置重试、回调监听等
     *
     * @param message 需要包装的消息
     * @param <T>     泛型
     * @return 包装好的 IMessageSend 对象
     */
    public <T extends WebSocketMessage> IMessageSend<T> obtainMessage(T message) {
        return new SendMessageWrapper<>(message);
    }

    /**
     * 发送一个 IMessageSend 对象
     *
     * @param messageSend 需要发送的message
     * @param <T>         泛型
     * @return 发送消息的ID，用于后续取消
     */
    public <T extends WebSocketMessage> int sendMessage(IMessageSend<T> messageSend) {
        return mWebSocketClient.sendMessageWrapper((SendMessageWrapper<T>) messageSend);
    }

    /**
     * App通过WebSocket发送消息
     * 使用此方法发送消息，一下参数将会默认：
     * 1.callback: null，不回调任何状态通知
     * 2.retryCont:默认为1；表示不重试
     * 3.retryInterval: 默认10秒 {@link SendMessageWrapper#RETRY_INTERVAL}
     *
     * @param webSocketMessage 待发送的消息
     * @return 发送消息的ID，用于后续取消
     */
    public <T extends WebSocketMessage> int sendMessage(T webSocketMessage) {
        return mWebSocketClient.sendMessageWrapper(new SendMessageWrapper<>(webSocketMessage));
    }

    /**
     * App通过WebSocket发送消息
     * 使用此方法发送消息，一下参数将会默认：
     * 1.callback: null，不回调任何状态通知
     * 2.retryCount: 设置为5次重试 {@link SendMessageWrapper#SendMessageWrapper}
     * 3.retryInterval: 默认10秒 {@link SendMessageWrapper#RETRY_INTERVAL}
     *
     * @param webSocketMessage 待发送的消息
     * @return 发送消息的ID，用于后续取消
     */
    public <T extends WebSocketMessage> int sendMessageRetry(T webSocketMessage) {
        return mWebSocketClient.sendMessageWrapper(new SendMessageWrapper<>(webSocketMessage).setRetryCount(SendMessageWrapper.RETRY_COUNT));
    }

    /**
     * 移除发送的消息
     *
     * @param sendId 发送的消息ID
     * @return 成功返回true；未找到（无此消息，或已经接收到Reply后自动移除）返回false
     */
    public boolean removeMessage(int sendId) {
        return mWebSocketClient.removeMessage(sendId);
    }

    public void connectWebSocket() {
        if (!isNetConnect()) {
            Log.d(TAG, "网络无连接");
            return;
        }
        mWebSocketClient.start();
    }

    public void closeWebSocket() {
        mWebSocketClient.stop();
    }

    public void setAppContext(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 使用 {@link cn.ktc.jkf.serverapi.data.json.websocket.MessageType#HEARTBEAT}替代了会议室HTTP心跳，
     * 因此需要将当前会议室ID设置到WS模块中
     * App上层使用此方法更新WS模块中的meetingId
     *
     * @param uid 当前用户ID
     * @param meetingId 当前会议室ID。如果未加入会议室则设置为null
     */
    public void updateMeetingId(Integer uid, Integer meetingId) {
        mWebSocketClient.updateMeetingId(uid, meetingId);
    }

    private boolean isNetConnect() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

}
