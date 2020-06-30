package cn.ktc.jkf.serverapi.websocket.list;

import cn.ktc.jkf.serverapi.data.json.websocket.SendMessageWrapper;

/**
 * RetryList（重试列表）
 * <p>
 * 使用3个列表维护数据，分别为：Send List, RetryList, ReplyList
 * 其他列表请参考：{@link SendListManager} {@link ReplyListManager}
 * <p>
 * 1.保存所有发送失败需要重试的
 * 2.如果ReplyList超时，也会放到这个列表中
 * 3.会判断重试次数是否已经用完。如果未用完会放到 {@link SendListManager} 中发送
 * 4.可能会回调到App中
 *
 * @author hq
 */
public class RetryListManager extends SortMessageListManager {
    /**
     * 列表操作
     */
    private final IListOperation operation;

    public RetryListManager(IListOperation listOperation) {
        this.operation = listOperation;
        startThread();
    }


    public void add(SendMessageWrapper message) {
        super.offer(message);
    }

    @Override
    protected void onNextMessage(SendMessageWrapper messageWrapper) {
        if (messageWrapper.onMessageRetry()) {
            messageWrapper.onMessageFailure();
        } else {
            //回调返回false，需要重试发送
            operation.addToSendList(messageWrapper);
        }
        //延时50ms，不要发送的那么快
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {

        }
    }
}
