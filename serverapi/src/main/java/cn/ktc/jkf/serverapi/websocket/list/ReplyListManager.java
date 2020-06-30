package cn.ktc.jkf.serverapi.websocket.list;

import cn.ktc.jkf.serverapi.data.json.websocket.SendMessageWrapper;
import cn.ktc.jkf.serverapi.data.json.websocket.WebSocketMessage;

/**
 * ReplyList（需回复列表）
 * <p>
 * 使用3个列表维护数据，分别为：Send List, RetryList, ReplyList
 * 其他列表请参考：{@link SendListManager} {@link RetryListManager}
 * <p>
 * 1.保存所有发送成功，但需要后台回复的
 * 2.如果后台回复成功，则回调App，并且从列表中移除
 * 3.如果后台回复超时，加入到 {@link RetryListManager} 并且移除
 *
 * @author hq
 */
public class ReplyListManager extends SortMessageListManager {
    private IListOperation operation;

    public ReplyListManager(IListOperation listOperation) {
        this.operation = listOperation;
        startThread();
    }

    public void add(SendMessageWrapper message) {
        super.offer(message);
    }

    /**
     * 接收到后台的消息后，需要判定是否需要Reply
     * WebSocketClient接收到后台消息后，调用此方法通知到ReplyList
     *
     * @param received 接收到后台的消息
     * @return true表示已经处理，上层无需在处理了
     */
    public boolean messageReplied(WebSocketMessage received) {
        SendMessageWrapper message = pollWithMsgId(received.getReplyId());
        if (message != null) {
            //接收到了回复
            boolean replied = message.onMessageReplied(true);
            if (!replied) {
                //需要再次重试
                message.updateSendTime();
                operation.addToRetryList(message);
            }
            return true;
        }
        return false;
    }

    /**
     * Reply的消息处理，肯定是超时才会触发
     * 如果是Server返回的，已经在 {@link #messageReplied(WebSocketMessage)} 中处理了
     *
     * @param message 下次发送的消息
     */
    @Override
    protected void onNextMessage(SendMessageWrapper message) {
        boolean replied = message.onMessageReplied(false);
        if (!replied) {
            operation.addToRetryList(message);
        }
        //延时50ms，不要发送的那么快
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {

        }
    }
}
