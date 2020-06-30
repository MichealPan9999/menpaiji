package cn.ktc.jkf.serverapi.websocket.list;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import cn.ktc.jkf.serverapi.data.json.websocket.SendMessageWrapper;
import cn.ktc.jkf.serverapi.data.json.websocket.message.EmptyMessage;

/**
 * SendList（待发送列表）
 * <p>
 * 使用3个列表维护数据，分别为：Send List, RetryList, ReplyList
 * 其他列表请参考：{@link RetryListManager} {@link ReplyListManager}
 * <p>
 * 1.保存所有要发送的消息
 * 2.如果发送失败，则将消息移动到 {@link RetryListManager}
 * 3.如果发送成功，如果需要Reply则移动到 {@link ReplyListManager}，否则直接移除
 *
 * @author hq
 */
public class SendListManager {
    /**
     * 列表操作
     */
    private IListOperation operation;
    /**
     * 判定是否允许运行
     */
    private boolean running = true;
    private final LinkedBlockingQueue<SendMessageWrapper> queue = new LinkedBlockingQueue<>();

    public SendListManager(IListOperation listOperation) {
        this.operation = listOperation;
        thread.start();
    }

    /**
     * 退出，销毁线程
     * 注意：一旦，该实例自动无效，必须要重新new一个SendListMessage实例
     *
     * @param ms 需要等待的毫秒数。0表示不等待线程结束，直接返回
     */
    public void exit(long ms) {
        running = false;
        queue.offer(new SendMessageWrapper<>(new EmptyMessage()));
        if (ms > 0) {
            try {
                thread.join(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将消息加入到待发送列表
     *
     * @param message 需要加入的消息
     */
    public void add(SendMessageWrapper message) {
        queue.offer(message);
    }

    public void addAll(List<SendMessageWrapper> list) {
        queue.addAll(list);
    }

    public void clear() {
        queue.clear();
    }

    public SendMessageWrapper remove(int sendId) {
        Iterator<SendMessageWrapper> iterator = queue.iterator();
        while (iterator.hasNext()) {
            SendMessageWrapper message = iterator.next();
            if (message.getSendId() == sendId) {
                iterator.remove();
                return message;
            }
        }
        return null;
    }

    private Thread thread = new Thread(() -> {
        while (running) {
            SendMessageWrapper message = null;
            try {
                message = queue.take();
            } catch (InterruptedException e) {

            }
            if (!running) {
                break;
            }
            if (message == null) {
                continue;
            }
            boolean send = operation.sendMessage(message);
            if (send) {
                //发送成功了，判断是否需要Reply
            } else {
                //放到Retry列表中
                operation.addToRetryList(message);
            }
            //延时50ms，不要发送的那么快
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {

            }
        }
    });
}
