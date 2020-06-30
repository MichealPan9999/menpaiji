package cn.ktc.jkf.serverapi.data.json.websocket;

import cn.ktc.jkf.serverapi.data.json.websocket.callback.IWebSocketMessageCallback;

/**
 * SendMessageWrapper暴露给App的接口
 * 用于设置重试次数、回调等
 *
 * @author hq
 */
public interface IMessageSend<T extends WebSocketMessage> {

    /**
     * 获取当前的ID，用于后续取消
     *
     * @return 当前发送消息的ID
     */
    int getSendId();

    /**
     * 设置Retry的时间间隔（默认 10_000 ms）
     *
     * @param retryInterval 重试时间间隔，<0设置为默认（单位：毫秒）
     * @return 当前对象
     */
    IMessageSend<T> setRetryInterval(int retryInterval);

    /**
     * 设置出错后重试次数（默认==0表示不重试）
     *
     * @param count 需要重试的次数
     * @return 当前对象
     */
    IMessageSend<T> setRetryCount(int count);

    /**
     * 设置消息回调
     *
     * @param callback 回调接口
     * @return 当前对象
     */
    IMessageSend<T> setCallback(IWebSocketMessageCallback callback);

    /**
     * 获取发送的消息
     *
     * @return 发送的消息
     */
    T getWebSocketMessage();

    /**
     * 获取重试时间间隔
     *
     * @return 重试时间间隔，单位：毫秒
     */
    int getRetryInterval();

    /**
     * 获取从发送到回复的耗时（毫秒）
     *
     * @return 耗时时间。<0=表示还未接收到Reply
     */
    long getReplyElapsedTime();
}
