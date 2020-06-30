package cn.ktc.jkf.serverapi.websocket;

import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import cn.ktc.jkf.serverapi.data.json.websocket.MessageType;
import cn.ktc.jkf.serverapi.data.json.websocket.SendMessageWrapper;
import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;
import cn.ktc.jkf.serverapi.data.json.websocket.callback.IWebSocketInitListener;
import cn.ktc.jkf.serverapi.data.json.websocket.callback.IWebSocketMessageListener;
import cn.ktc.jkf.serverapi.data.json.websocket.callback.IWebSocketStateListener;
import cn.ktc.jkf.serverapi.data.json.websocket.message.HeartbeatMessage;
import cn.ktc.jkf.serverapi.data.json.websocket.message.InitMessage;
import cn.ktc.jkf.serverapi.utils.GZipUtil;
import cn.ktc.jkf.serverapi.websocket.list.IListOperation;
import cn.ktc.jkf.serverapi.websocket.list.ReplyListManager;
import cn.ktc.jkf.serverapi.websocket.list.RetryListManager;
import cn.ktc.jkf.serverapi.websocket.list.SendListManager;
import cn.ktc.jkf.utils.Log;
import cn.ktc.jkf.utils.RxHelper;
import cn.ktc.jkf.utils.SafeHandler;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * 基于okhttp创建一个WebSocket Client
 */
class WebSocketClient implements SafeHandler.Callback {
    private static String TAG = WebSocketClient.class.getSimpleName();
    /**
     * 是否使用{@link #sendList}发送数据
     * true: 发送数据之前先放到{@link #sendList}中
     * false: 直接使用 {@link #sendMessageWrapper(SendMessageWrapper)}发送
     */
    private static final boolean ENABLE_SENDLIST = true;
    /**
     * 发送失败时是否重试
     * true: 使用{@link #retryList}
     * false: 失败后不管了。例如在皓丽白板中发送笔迹数据
     */
    private static final boolean ENABLE_RETRYLIST = true;
    /**
     * 是否使用 {@link #replyList}
     */
    private static final boolean ENABLE_REPLYLIST = true;
    /**
     * WebSocket的连接超时。
     * WebSocket使用Okhttp实现，实际上是设置OkHttp的超时
     */
    private static final int TIMEOUT_WSCONNECT = 10_000;
    /**
     * WebSocket的读超时
     */
    private static final int TIMEOUT_WSREAD = 10_000;
    /**
     * WebSocket的写超时
     */
    private static final int TIMEOUT_WSWRITE = 10_000;
    /**
     * 定义一些Handler的消息标识
     */
    /**
     * 发送心跳
     * 心跳发送规则如下：
     * 1.接收到APP_INIT消息后，延迟1秒发送HEARTBEATG
     * 2.同时设置{@link #INTERVAL_HEARTBEAT_SHORT}定时器，用于发送HEARTBEAT
     * 3.如果接收到 HEARTBEAT 消息，则设置 {@link #INTERVAL_HEARTBEAT} 间隔发送
     */
    private static final int MESSAGE_HEARTBEAT = 1;
    private static final int INTERVAL_HEARTBEAT = 15_000;
    private static final int INTERVAL_HEARTBEAT_SHORT = 8_000;
    /**
     * APP_INIT消息。WS一旦连接就要发送
     */
    private static final int MESSAGE_APPINIT = 2;
    private static final int INTERVAL_APPINIT = 5_000;
    /**
     * 检查WebSocket是否需要重新连接
     * 30秒未接收到后台的任何回应，表示需要重新连接
     */
    private static final int MESSAGE_WS_RECONNECT = 3;
    /**
     * 判断WS是否断开，并重新连接的时间间隔
     * 必须要大于读写WS的超时设置
     * {@link #TIMEOUT_WSCONNECT}, {@link #TIMEOUT_WSREAD}， {@link #TIMEOUT_WSWRITE}
     **/
    private static final int INTERVAL_WS_RECONNECT = 32_000;

    /**
     * 接收到后台的APP_INIT回复，需要将RetryList和ReplyList中的消息立即发送
     * 使用Handler传递到主线程中处理
     */
    private static final int MESSAGE_WS_INIT_REPLY = 4;

    /**
     * 检测到一段时间未发出和收到后台消息（不包括心跳），表示WebSocket为空闲状态
     * 回调到APP做处理
     */
    private static final int MESSAGE_WEBSOCKET_FREE = 5;
    private static final int INTERVAL_WEBSOCKET_FREE = 5_000;

    private static final boolean ISMERGELOCAL = false;

    private WebSocket mSocket;
    private OkHttpClient mOkHttpClient;
    private MWebSocketListener mWebSocketListener;
    private String baseUrl;
    /**
     * 当前是否执行
     * 只要用户调用了connect，则设置为true
     * 调用了disconnect，则设置为false
     */
    private boolean running = false;
    /**
     * 标记WS状态
     */
    private WSState wsState = WSState.DISCONNECTED;

    /**
     * 接收到后台的 APP_INIT 消息回复
     * App发送消息前，必须要确保后台正确响应了APP_INIT
     * 如果还未接收到，则将所有的消息放到RetryList中
     */
    private boolean isMessageAppInit = false;

    /**
     * 执行WS连接的次数
     * 一分钟内重连5次或以上回调到APP提示用户
     */
    private int reConnectTimes = 0;

    private static final int MAX_CONNECT_TIMES = 5;

    /**
     * WS第一次连接的时间
     * 若5次以内重连时间超过1分钟，则重置为当前时间
     */
    private long firstReConnectTime;

    private static final int MAX_CONNECT_TIME = 60 * 1000;

    private SendListManager sendList;
    private RetryListManager retryList;
    private ReplyListManager replyList;

    /**
     * 保存App消息回调注册列表
     */
    private MessageListener messageListener = new MessageListener();
    /**
     * 保存WebSocket状态变更的回调通知
     */
    private StateListener stateListener = new StateListener();

    /**
     * 保存WebSocket init的回调通知
     */
    private WebsocketInitListener initListener = new WebsocketInitListener();

    private SafeHandler handler = new SafeHandler(this);

    private Integer uid;
    private Integer meetingId;
    /**
     * 上次发送 HEARTBEAT 消息的时间
     * 下次间隔应该是 {@link #INTERVAL_HEARTBEAT} 与这个时间相减
     */
    private long heartbeatTime;

    WebSocketClient() {
    }

    public void setInfo(String wsaddr) {
        baseUrl = wsaddr;
    }

    /**
     * WebSocket状态通知
     *
     * @param listener 回调
     * @param group    分组名称
     */
    void addStateListener(IWebSocketStateListener listener, String group) {
        stateListener.add(0, listener, false, group);
    }

    void removeStateListener(IWebSocketStateListener listener) {
        stateListener.remove(0, listener);
    }

    /**
     * WebSocket init后的回调
     *
     * @param listener 回调
     * @param group    分组名称
     */
    void addInitListener(IWebSocketInitListener listener, String group) {
        initListener.add(0, listener, false, group);
    }

    void removeInitListener(IWebSocketInitListener listener) {
        initListener.remove(0, listener);
    }

    /**
     * 将消息回调增加到Manager统一管理
     *
     * @param msgType    消息类型
     * @param listener   监听器
     * @param mainThread 是否在主线程中回调
     * @param group      分组名称
     */
    void addMessageReceivedListener(int msgType, IWebSocketMessageListener listener, boolean mainThread, String group) {
        messageListener.add(msgType, listener, mainThread, group);
    }

    /**
     * 移除接收消息监听器
     *
     * @param msgType  消息类型。0表示忽略，只需要比对 listener
     * @param listener 监听器
     */
    void removeMessageReceivedListener(int msgType, IWebSocketMessageListener listener) {
        messageListener.remove(msgType, listener);
    }

    void removeMessageReceivedListener(String group) {
        messageListener.remove(group);
    }

    void updateMeetingId(Integer uid, Integer meetingId) {
        this.uid = uid;
        this.meetingId = meetingId;
    }

    /**
     * 开始WebSocket
     * 理论上只需要初始化一次，除非：
     * 1.调用了 {@link #stop()} 终止
     * 2.WebSocket地址变更，需要重新连接
     */
    public void start() {
        if (running) {
            return;
        }
        running = true;
        if (mWebSocketListener == null) {
            mWebSocketListener = new MWebSocketListener();
        }
        if (ENABLE_SENDLIST) {
            sendList = new SendListManager(operation);
        }
        if (ENABLE_RETRYLIST) {
            retryList = new RetryListManager(operation);
        }
        if (ENABLE_REPLYLIST) {
            replyList = new ReplyListManager(operation);
        }

        connectWebSocket();
    }

    /**
     * 停止WebSocket，用于App退出时
     */
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        handler.removeCallbacksAndMessages(null);
        disConnectWebSocket(true);
        if (sendList != null) {
            sendList.exit(0);
            sendList = null;
        }
        if (retryList != null) {
            retryList.exit();
            retryList = null;
        }
        if (replyList != null) {
            replyList.exit();
            replyList = null;
        }
    }

    private void connectWebSocket() {
        Log.ws("connectWebSocket, wsState=" + wsState);
        if ((wsState != WSState.DISCONNECTED) && (wsState != WSState.DISCONNECTING)) {
            return;
        }

        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS,ConnectionSpec.CLEARTEXT))
                    .readTimeout(TIMEOUT_WSREAD, TimeUnit.MILLISECONDS)
                    .writeTimeout(TIMEOUT_WSWRITE, TimeUnit.MILLISECONDS)
                    .connectTimeout(TIMEOUT_WSCONNECT, TimeUnit.MILLISECONDS)
                    .build();
        }
        if (TextUtils.isEmpty(baseUrl)) {
            return;
        }
        Log.ws("Connect to WS ..." + baseUrl);
        Request request = new Request.Builder().url(baseUrl).build();
        wsState = WSState.CONNECTING;
        mOkHttpClient.newWebSocket(request, mWebSocketListener);

        if (reConnectTimes == 0) {
            firstReConnectTime = System.currentTimeMillis();
        } else if (reConnectTimes >= MAX_CONNECT_TIMES) {
            stateListener.onSocketReConnectTimesOut();
            reConnectTimes = 0;
        } else {
            if (System.currentTimeMillis() - firstReConnectTime > MAX_CONNECT_TIME) {
                firstReConnectTime = System.currentTimeMillis();
            }
        }
        reConnectTimes++;
    }

    private void disConnectWebSocket(boolean clearData) {
        Log.ws("disConnectWebSocket: ws=" + mSocket);
        if (wsState != WSState.DISCONNECTED) {
            wsState = WSState.DISCONNECTING;
            isMessageAppInit = false;
        }
        if (mSocket != null) {
            Log.ws("disConnectWebSocket close ...");
            mSocket.close(1000, null);
            messageListener.remove(MESSAGE_APPINIT, appInitListener);
            if (clearData) {
                if (sendList != null) {
                    sendList.clear();
                }
                if (retryList != null) {
                    retryList.clear(null);
                }
                if (replyList != null) {
                    replyList.clear(null);
                }
            }
        }
        if (mOkHttpClient != null) {
            mOkHttpClient.dispatcher().executorService().shutdown();
            mOkHttpClient = null;
        }
    }

    /**
     * 将消息添加到消息队列
     */
    int sendMessageWrapper(SendMessageWrapper message) {
        operation.addToSendList(message);
        return message.getSendId();
    }

    /**
     * 移除指定的消息
     *
     * @param sendId 指定的消息
     * @return true表示找到并移除；false表示未找到（无此消息，或消息已经Reply后移除、或Retry次数到了移除）
     */
    boolean removeMessage(int sendId) {
        if (sendList == null) {
            return false;
        }
        if (null != sendList.remove(sendId)) {
            return true;
        }

        if (null != retryList.remove(sendId)) {
            return true;
        }

        if (null != replyList.remove(sendId)) {
            return true;
        }

        return false;
    }

    /**
     * 处理从服务器返回的消息，检测回复队列中是否存在与该消息replyId对应的消息，若有，则移除
     */
    private void processMessage(String text) {
        WebSocketMessage message = WebSocketMessage.fromJson(text);
        if (message == null) {
            //此处是有异常的
            Log.wsr("WS Received Exception, MSG=" + text);
            return;
        }
        Log.wsr("WS Received RID=", message.getReplyId(), " TYPE=", MessageType.getDebugName(message.getMsgType()), "[" + message.getMsgType() + "]", message.getJsonObject());
        //对心跳发送时间进行复位
        handler.sendMessageOnly(MESSAGE_WS_RECONNECT, INTERVAL_WS_RECONNECT);
        //App注册的消息监听回调
        messageListener.onMessageReceived(message);
        if (message.getMsgType() == MessageType.HEARTBEAT) {
            //是心跳消息，复位
            long delay = SystemClock.uptimeMillis() - heartbeatTime;
            if (meetingId != null) {
                delay = INTERVAL_HEARTBEAT_SHORT - Math.min(delay, INTERVAL_HEARTBEAT_SHORT);
            } else {
                delay = INTERVAL_HEARTBEAT - Math.min(delay, INTERVAL_HEARTBEAT);
            }
            handler.sendMessageOnly(MESSAGE_HEARTBEAT, (int) delay);
        }
        //若replylist能找到消息，则表示是从本机发出不需做回调处理
        if (replyList != null && replyList.messageReplied(message)) {
            return;
        }
        //复位WebSocket空闲状态
        handler.sendMessageOnly(MESSAGE_WEBSOCKET_FREE, INTERVAL_WEBSOCKET_FREE);
    }

    @Override
    public void handlerMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_HEARTBEAT:
                operation.sendMessage(new SendMessageWrapper<>(new HeartbeatMessage().setMeetingId(meetingId, uid)));
                handler.sendMessageOnly(MESSAGE_HEARTBEAT, INTERVAL_HEARTBEAT_SHORT);
                heartbeatTime = SystemClock.uptimeMillis();
                break;
            case MESSAGE_APPINIT:
                operation.addToSendList(new SendMessageWrapper<>(new InitMessage()));
                handler.sendMessageOnly(MESSAGE_APPINIT, INTERVAL_APPINIT);
                break;
            case MESSAGE_WS_RECONNECT:
                disConnectWebSocket(false);
                if (running) {
                    connectWebSocket();
                    handler.sendMessageOnly(MESSAGE_WS_RECONNECT, INTERVAL_WS_RECONNECT);
                }
                break;
            case MESSAGE_WS_INIT_REPLY:
                initListener.onWebSocketInit();
                ArrayList<SendMessageWrapper> list = new ArrayList<>();
                if (retryList != null) {
                    retryList.clear(list);
                }
                if (sendList != null) {
                    sendList.addAll(list);
                }
                break;
            case MESSAGE_WEBSOCKET_FREE:
                stateListener.onSocketFree();
                break;
            default:
                break;
        }
    }

    /**
     * 当WebSocket关闭时，需要删除一些消息
     */
    private void removeWSMessageHandler() {
        handler.removeMessages(MESSAGE_HEARTBEAT, MESSAGE_APPINIT, MESSAGE_WS_RECONNECT, MESSAGE_WEBSOCKET_FREE);
    }

    /**
     * 注册一个消息回调，接收到 APP_INIT 后取消发送
     */
    private IWebSocketMessageListener appInitListener = new IWebSocketMessageListener() {
        @Override
        public WebSocketMessage onMessageReceived(WebSocketMessage message) {
            //接收到APP_INIT的回复，不再需要发送了
            isMessageAppInit = true;
            handler.removeMessages(MESSAGE_APPINIT);
            messageListener.remove(MESSAGE_APPINIT, this);
            handler.sendMessageOnly(MESSAGE_WS_INIT_REPLY, 0);
            //开始发送心跳 HEARTBEAT
            handler.sendMessageOnly(MESSAGE_HEARTBEAT, 1000);
            return null;
        }
    };

    private final class MWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            Log.ws("onOpen 连接成功！");
            wsState = WSState.CONNECTED;
            mSocket = webSocket;
            stateListener.onNetworkConnected();
            stateListener.onConnected();
            //发送APP_INIT消息
            messageListener.remove(MessageType.APP_INIT, appInitListener);
            messageListener.add(MessageType.APP_INIT, appInitListener, false, null);
            handler.sendMessageOnly(MESSAGE_APPINIT, 5);
            //开启定义，判断WS是否需要连接
            handler.sendMessageOnly(MESSAGE_WS_RECONNECT, INTERVAL_WS_RECONNECT);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
            String message = GZipUtil.uncompress(bytes.toByteArray(),0,bytes.size());
            if (!TextUtils.isEmpty(message)) {
                processMessage(message);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            processMessage(text);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Log.ws("onClosed reason:" + code + ", reason=" + reason);
            if (mSocket == webSocket) {
                wsState = WSState.DISCONNECTED;
                stateListener.onDisconnect(code);
                removeWSMessageHandler();
                mSocket = null;
                isMessageAppInit = false;
            }
            if (running) {
                //立即开始重试
                handler.sendMessageOnly(MESSAGE_WS_RECONNECT, 1_000);
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            Log.ws("onClosing reason:" + code + ", reason=" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            Log.ws("onFailure:" + t.getMessage() + ", ws0=" + mSocket + ", ws=" + webSocket);
            if (mSocket == webSocket) {
                wsState = WSState.DISCONNECTED;
                stateListener.onFailure(t);
                removeWSMessageHandler();
                mSocket = null;
                isMessageAppInit = false;
            }
            if (running) {
                //立即开始重试
                handler.sendMessageOnly(MESSAGE_WS_RECONNECT, 1_000);
            }
        }
    }

    private IListOperation operation = new IListOperation() {
        @Override
        public boolean isNetworkConnected() {
            return true;
        }

        @Override
        public boolean isWebsocketConnected() {
            return wsState == WSState.CONNECTED;
        }

        @Override
        public boolean sendMessage(SendMessageWrapper message) {
            boolean send = false;
            boolean addToRetry = ENABLE_RETRYLIST;
            //在收到APP_INIT消息REPLY之前，其他的消息都不能发送
            boolean allow = (wsState == WSState.CONNECTED) && (isMessageAppInit || message.getMessageType() == MessageType.APP_INIT) && mSocket != null;
            if (allow) {
                final String text = message.getMessageText();
                Log.wss("WS Send type=", MessageType.getDebugName(message.getMessageType()), "[" + message.getMessageType() + "]", message.getWebSocketMessage());
                ByteString snd = GZipUtil.compress2(text);
                send = snd != null && mSocket != null &&mSocket.send(snd);
                message.updateSendTime()
                        .updateSendCount();
                message.onMessageSend(send);
                if (send) {
                    if (ENABLE_RETRYLIST) {
                        addToReplyList(message);
                        addToRetry = false;
                    }
                    //复位WebSocket空闲状态
                    //if (message.getMessageType() != MessageType.HEARTBEAT) {
                        handler.sendMessageOnly(MESSAGE_WEBSOCKET_FREE, INTERVAL_WEBSOCKET_FREE);
                    //}
                } else {
                    message.onNetworkEerror();
                    addToRetry = message.canRetry();
                }
            }
            if (addToRetry) {
                //加入到Retry列表
                message.updateSendTime();
                addToRetryList(message);
            }
            return send;
        }

        @Override
        public void addToSendList(SendMessageWrapper message) {
            if (ENABLE_SENDLIST) {
                sendList.add(message);
            } else {
                sendMessage(message);
            }
        }

        @Override
        public void addToRetryList(SendMessageWrapper message) {
            if (ENABLE_RETRYLIST) {
                retryList.add(message);
            }
        }

        @Override
        public void addToReplyList(SendMessageWrapper message) {
            if (ENABLE_REPLYLIST) {
                replyList.add(message);
            }
        }
    };

    /**
     * 定义WebSocket的状态
     */
    private enum WSState {
        /**
         * 已断开。连接失败也会置为此状态
         */
        DISCONNECTED,
        /**
         * 正在发送指令，准备断开
         */
        DISCONNECTING,
        /**
         * 已连接。可能实际上已经断开，但还没来及更新状态
         */
        CONNECTED,
        /**
         * 正在连接中
         * 一般指App发送了连接请求，但还未返回。
         * 可能最终连接失败
         */
        CONNECTING
    }

    private class MessageListener extends MessageListenerManager<IWebSocketMessageListener> {
        /**
         * 接收到消息后，通知到App上层
         *
         * @param message
         */
        public void onMessageReceived(WebSocketMessage message) {
            int msgType = message.getMsgType();
            synchronized (this) {
                for (Data data : list) {
                    if (data.msgType == msgType) {
                        if (data.mainThread) {
                            RxHelper.excuteMainThread((param -> {
                                WebSocketMessage needReplyMessage = ((IWebSocketMessageListener) data.listener).onMessageReceived(message);
                                if (needReplyMessage != null) {
                                    operation.addToSendList(new SendMessageWrapper<>(needReplyMessage).setRetryCount(0));
                                }
                            }), message);
                        } else {
                            RxHelper.excuteIoThread((param -> {
                                WebSocketMessage needReplyMessage = ((IWebSocketMessageListener) data.listener).onMessageReceived(message);
                                if (needReplyMessage != null) {
                                    operation.addToSendList(new SendMessageWrapper<>(needReplyMessage).setRetryCount(0));
                                }
                            }), message);
                        }
                    }
                }
            }
        }
    }

    private class StateListener extends MessageListenerManager<IWebSocketStateListener> {
        public void onNetworkConnected() {
            excuteAll(data -> ((IWebSocketStateListener) data.listener).onNetworkConnected());

        }

        public void onConnected() {
            excuteAll(data -> ((IWebSocketStateListener) data.listener).onSocketConnected());
        }

        public void onDisconnect(int code) {
            excuteAll(data -> ((IWebSocketStateListener) data.listener).onSocketClosed(code));
        }

        public void onFailure(Throwable t) {
            excuteAll(data -> ((IWebSocketStateListener) data.listener).onSocketFailure(t));
        }

        public void onSocketReConnectTimesOut() {
            excuteAll(data -> ((IWebSocketStateListener) data.listener).onSocketReConnectTimesOut());
        }

        public void onSocketFree() {
            excuteAll(data -> ((IWebSocketStateListener) data.listener).onSocketFree());
        }
    }

    private class WebsocketInitListener extends MessageListenerManager<IWebSocketInitListener> {
        public void onWebSocketInit() {
            excuteAll(data -> ((IWebSocketInitListener) data.listener).onWebSocketInit());
        }

        public void onWebSocketRetry(ArrayList<SendMessageWrapper> list) {
            excuteAll(data -> ((IWebSocketInitListener) data.listener).onWebSocketRetry(list));
        }
    }
}
