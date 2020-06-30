package cn.ktc.jkf.serverapi.data.json.websocket;

import android.os.SystemClock;

import cn.ktc.jkf.serverapi.data.IUserData;
import cn.ktc.jkf.serverapi.data.json.JsonHelper;
import cn.ktc.jkf.serverapi.data.json.websocket.callback.IWebSocketMessageCallback;

/**
 * WebSocket 发送的消息，对 WebSocketMessage 的封装
 *
 * @author hq
 */
public class SendMessageWrapper<T extends WebSocketMessage> implements IMessageSend<T>, IUserData {
    /**
     * 如果需要重试发送消息，则默认重试的次数
     */
    public static final int RETRY_COUNT = 5;

    /**
     * 定义下一次重试的时间
     * 当前定义为10s，后续可以更改
     */
    private static final int RETRY_INTERVAL = 10_000;

    public SendMessageWrapper(T message) {
        this.message = message;
        this.addTime = SystemClock.uptimeMillis();
    }

    private Object userData;

    /**
     * 保存实际发送的消息
     */
    private T message;

    /**
     * 增加到SendList中的时间
     */
    private final long addTime;

    /**
     * 上次发送的时间。不管成功还是失败
     * 如果是网络断开，则不更新
     */
    private long sendTime;

    /**
     * 下一次重试发送的时间。用于Retry时的时间控制
     */
    private long nextSendTime;

    /**
     * 发送失败后，重试的时间间隔
     */
    private int retryInterval = RETRY_INTERVAL;
    /**
     * 收到后台回复的时间
     */
    private long replyTime;

    /**
     * 已发送次数
     * 不管是否成功，发送一次就+1
     */
    private int sendCount = 0;

    /**
     * 优先级
     */
    private int priorit = 10;

    /**
     * 总的允许重试的次数
     */
    private int retryCount = 0;

    /**
     * 已经重试了的次数
     */
    private int retryIndex = 0;

    /**
     * 是否需要回复
     * 只有接收到后台REPLY后才回调
     */
    private boolean needRetry = false;

    private IWebSocketMessageCallback callback;

    public int getSendCount() {
        return sendCount;
    }

    /**
     * 更新已发送了的次数（不管是否成功）
     * 只要调用了发送（ws发送）就+1
     *
     * @return 当前对象，便于链式调用
     */
    public SendMessageWrapper<T> updateSendCount() {
        sendCount++;
        return this;
    }

    /**
     * 执行重试Retry后调用，将RetryIndex数值+1
     *
     * @return
     */
    public SendMessageWrapper<T> updateRetryIndex() {
        retryIndex++;
        return this;
    }

    public long getReplyTime() {
        return replyTime;
    }

    public long getSendTime() {
        return sendTime;
    }

    public long getAddTime() {
        return addTime;
    }

    public int getPriorit() {
        return priorit;
    }

    public boolean canRetry() {
        return retryIndex < retryCount;
    }

    /**
     * 更新当前发送的时间。
     * 注意：调用Ws.send()时必须要调用该方法，用于更新时间，否则在Retry时设置的重试时间会错误
     *
     * @return 当前对象，便于链式调用
     */
    public SendMessageWrapper<T> updateSendTime() {
        sendTime = SystemClock.uptimeMillis();
        nextSendTime = sendTime + retryInterval;
        return this;
    }

    /**
     * 获取下次的send时间(SystemClock.uptimeMillis的时间)
     *
     * @return 下次Send时间
     */
    public long getNextSendTime() {
        return nextSendTime;
    }

    /**
     * 获取下一次的发送时间差。一般用于Retry中控制发送时间
     *
     * @return 下一次的发送时间差（相对于当前时间）
     */
    public long getNextSendTimeDelay() {
        long current = SystemClock.uptimeMillis();
        return nextSendTime < current ? 0 : nextSendTime - current;
    }

    /**
     * 接收到后台回复后调用
     *
     * @return 当前对象，便于链式调用
     */
    public SendMessageWrapper<T> updateReplied() {
        replyTime = SystemClock.uptimeMillis();
        return this;
    }

    public int getMessageType() {
        return message.getMsgType();
    }

    /**
     * 获取要发送的消息
     *
     * @return 需要发送的消息
     */
    public String getMessageText() {
        String text = message == null ? null : JsonHelper.toJson(message);
        return text == null ? "" : text;
    }

    /**
     * WS.Send()状态回调
     * 注意：如果没有设置RetryCount，当send=false时将不会由后面的回调
     *
     * @param send WS.Send的返回值
     */
    public void onMessageSend(boolean send) {
        if (callback != null) {
            callback.onSend(message, send);
        }
    }

    /**
     * 检测到网络异常，且Retry重试次数已经用完的回调
     */
    public void onNetworkEerror() {
        if (callback != null) {
            callback.onFailure(message, true);
        }
    }

    /**
     * 消息是否需要继续重试
     *
     * @return true 表示还需要重试发送
     */
    public boolean onMessageRetry() {
        if (callback != null) {
            return callback.onRetry(message, retryIndex, retryCount);
        }
        return canRetry();
    }

    /**
     * 消息发送失败的通知
     * 注意：如果是网络异常，请使用 {@link #onNetworkEerror()}
     */
    public void onMessageFailure() {
        if (callback != null) {
            callback.onFailure(message, false);
        }
    }

    /**
     * 接收到后台的Reply后的回到
     *
     * @param replied 是否接收到后台Reply回到。false表示未接收到且超时的
     * @return 默认true，表示已经处理，无需再次Retry了
     */
    public boolean onMessageReplied(boolean replied) {
        if (callback != null) {
            return callback.onReplied(message, replied);
        }
        return true;
    }

    @Override
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    /**
     * 获取当前的ID，用于后续取消
     *
     * @return 当前发送消息的ID
     */
    @Override
    public int getSendId() {
        return message.getMsgId();
    }

    /**
     * 设置Retry的时间间隔
     *
     * @param retryInterval 需要设置的间隔。<0设置为默认间隔（单位：毫秒）
     * @return 当前对象，便于链式调用
     */
    @Override
    public SendMessageWrapper<T> setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval < 0 ? RETRY_INTERVAL : retryInterval;
        return this;
    }

    /**
     * 设置重试次数
     *
     * @param count 需要重试的次数。<=0表示无需重试
     * @return 当前对象，便于链式调用
     */
    @Override
    public SendMessageWrapper<T> setRetryCount(int count) {
        this.retryCount = count <= 0 ? 0 : count;
        return this;
    }

    /**
     * 设置消息回调
     *
     * @param callback 回调接口
     * @return 当前对象，便于链式调用
     */
    @Override
    public SendMessageWrapper<T> setCallback(IWebSocketMessageCallback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * 获取需要发送的消息
     *
     * @return 待发送的消息
     */
    @Override
    public T getWebSocketMessage() {
        return message;
    }


    @Override
    public int getRetryInterval() {
        return retryInterval;
    }

    @Override
    public long getReplyElapsedTime() {
        long delta = replyTime - addTime;
        return delta < 0 ? -1 : delta;
    }
}