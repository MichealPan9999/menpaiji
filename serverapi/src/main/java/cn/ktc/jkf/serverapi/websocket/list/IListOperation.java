package cn.ktc.jkf.serverapi.websocket.list;

import cn.ktc.jkf.serverapi.data.json.websocket.SendMessageWrapper;

/**
 * 维护几个列表的接口，由Client实现
 *
 * @author hq
 */
public interface IListOperation {
    /**
     * 网络是否连接
     *
     * @return 返回当前网络是否连接
     */
    boolean isNetworkConnected();

    /**
     * 返回WebSocket是否连接
     *
     * @return WS是否连接
     */
    boolean isWebsocketConnected();

    /**
     * 将消息通过WebSocket发送出去
     * 注意：仅仅实现发送操作，超时、超时次数等无需处理
     *
     * @param message 发送的消息
     * @return 成功返回true
     */
    boolean sendMessage(SendMessageWrapper message);

    /**
     * 加入到待发送列表{@link SendListManager}
     *
     * @param message 待发送的消息
     */
    void addToSendList(SendMessageWrapper message);

    /**
     * 加入到待重试列表 {@link RetryListManager}
     *
     * @param message 待重试的消息
     */
    void addToRetryList(SendMessageWrapper message);


    /**
     * 加入到待回复列表 {@link ReplyListManager}
     *
     * @param message 待回复的消息
     */
    void addToReplyList(SendMessageWrapper message);
}
