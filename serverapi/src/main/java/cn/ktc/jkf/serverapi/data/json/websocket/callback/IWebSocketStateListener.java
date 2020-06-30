package cn.ktc.jkf.serverapi.data.json.websocket.callback;

/**
 * WebSocket连接状态的监听
 * 当WS不同的连接状态时的回调监听
 *
 * @author hq
 */
public interface IWebSocketStateListener {
    /**
     * 网络恢复连接
     */
    default void onNetworkConnected() {
    }

    /**
     * 网络断开连接
     */
    default void onNetworkDisconnected() {
    }

    /**
     * WebSocket连接上
     */
    default void onSocketConnected() {
    }

    /**
     * WebSocket关闭
     *
     * @param code 状态码
     */
    default void onSocketClosed(int code) {
    }

    /**
     * WebSocket出现异常
     *
     * @param t 异常信息
     */
    default void onSocketFailure(Throwable t) {
    }

    /**
     * WebSocket连续断开重连
     */
    default void onSocketReConnectTimesOut() {
    }

    /**
     * WebSocket空闲状态
     */
    default void onSocketFree() {
    }
}
