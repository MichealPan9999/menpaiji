package cn.ktc.jkf.serverapi.data.json.websocket.callback;

import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;

/**
 * App发送消息时，如果调用WebSocketApi.sendMessage()时指定了Callback，WS在不同操作时的状态回调通知
 * 1.WS是否发送成功
 * 2.WS是否超时
 * 3.WS是否接收到REPLY
 * <p>
 * 通用说明：
 * 1.返回true表示已经处理，底层无需重新加入到RetryList了
 * 2.如果需要底层重新加入到RetryList，请返回false
 *
 * @author hq
 */
public interface IWebSocketMessageCallback {

    /**
     * WebSocket 执行Send后的回调
     * 注意：如果WS.send()之前检测到网络未连接，则不会回调
     *
     * @param message 待发送的消息
     * @param result  WS.send()返回值
     */
    default void onSend(WebSocketMessage message, boolean result) {
    }

    /**
     * 发送失败后加入到RetryList中，准备重试时的回调
     *
     * @param message    待发送的消息
     * @param index      当前重试的序号
     * @param retryCount 总的设置的重试次数
     * @return true表示响应成功，或者重试次数已经达到，不再重试
     * false表示需要继续重试发送
     */
    default boolean onRetry(WebSocketMessage message, int index, int retryCount) {
        return index >= retryCount;
    }

    /**
     * 发送失败的通知
     * 1.当重试次数用完后还未发送成功的回调
     * 2.网络异常（网络未连接、WebSocket还未连接上）
     *
     * @param message 消息
     * @param network true表示未网络未连接；false表示每次都是WS.send()返回false
     */
    default void onFailure(WebSocketMessage message, boolean network) {

    }

    /**
     * 如果指定了需要Reply，则接收到Reply后的回调通知
     * 回调到这里表示WS.send()成功了
     *
     * @param message 消息
     * @param success true表示接收到后台的Reply；false表示超时未接收到
     * @return true表示响应成功，从ReplyList中移除，后续不再Retry（即使出错了）
     * false表示需要加入到 RetryList中（后面根据retryCount判断）
     */
    default boolean onReplied(WebSocketMessage message, boolean success) {
        return success;
    }
}
