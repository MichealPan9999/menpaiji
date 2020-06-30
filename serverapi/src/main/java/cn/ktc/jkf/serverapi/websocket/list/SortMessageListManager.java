package cn.ktc.jkf.serverapi.websocket.list;

import android.os.SystemClock;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import cn.ktc.jkf.serverapi.data.json.websocket.SendMessageWrapper;

/**
 * 对消息 SendMessageWrapper列表的封装
 * 1.按照下次发送的时间排序，由小到大
 * 2.同步操作
 * 3.其他操作的封装
 *
 * @author hq
 */
public abstract class SortMessageListManager {
    /**
     * 判定是否允许运行
     */
    private boolean running = true;

    /**
     * 保存Message的链表
     */
    private LinkedList<SendMessageWrapper> list = new LinkedList<>();

    private final Object waitObj = new Object();

    private final ElementCheckPeek elementCheckPeek = new ElementCheckPeek();


    /**
     * 退出，销毁线程
     * 注意：一旦调用，该实例自动无效，必须要重新new一个SendListMessage实例
     */
    public void exit() {
        running = false;
        clear(null);
        synchronized (waitObj) {
            waitObj.notifyAll();
        }
    }

    /**
     * 将一个Message插入到列表中
     * 按照下次发送的时间排序
     *
     * @param message
     */
    public synchronized void offer(SendMessageWrapper message) {
        ListIterator<SendMessageWrapper> iterator = list.listIterator();
        while (iterator.hasNext()) {
            SendMessageWrapper wrapper = iterator.next();
            if (wrapper.getNextSendTime() > message.getNextSendTime()) {
                iterator.add(message);
                synchronized (waitObj) {
                    waitObj.notifyAll();
                }
                return;
            }
        }
        //加到末尾
        list.add(message);
        synchronized (waitObj) {
            waitObj.notifyAll();
        }
    }

    /**
     * 清空列表
     *
     * @param saved !=null表示将所有的元素复制到这里
     *              比如在WS恢复连接时，需要将所有Reply的加入到Retry中，并且清空Reply
     */
    public synchronized void clear(List<SendMessageWrapper> saved) {
        if (saved != null) {
            saved.addAll(list);
        }
        list.clear();
    }

    public synchronized SendMessageWrapper remove(int sendId) {
        ListIterator<SendMessageWrapper> iterator = list.listIterator();
        while (iterator.hasNext()) {
            SendMessageWrapper wrapper = iterator.next();
            if (wrapper.getSendId() == sendId) {
                iterator.remove();
                return wrapper;
            }
        }
        return null;
    }

    /**
     * 获取指定的msgId的元素
     * 用于Reply中，接收到后台回复后查找
     *
     * @return 返回找到的元素；null表示未找到
     * !=null时会从列表中移除找到的项
     */
    public synchronized SendMessageWrapper pollWithMsgId(int msgId) {
        Iterator<SendMessageWrapper> iterator = list.iterator();
        while (iterator.hasNext()) {
            SendMessageWrapper message = iterator.next();
            if (message.getWebSocketMessage().getMsgId() == msgId) {
                iterator.remove();
                return message;
            }
        }
        return null;
    }


    /**
     * 判断第一个元素是否时间到了
     *
     * @return null表示获取失败，需要继续等待;
     * !=null表示获取到元素了，可以继续执行
     * !=null 时获取的元素，会自动从list移除
     */
    private synchronized ElementCheckPeek checkAndPoll() {
        SendMessageWrapper message = list.peek();
        if (message == null) {
            elementCheckPeek.message = null;
            elementCheckPeek.waitMills = Long.MAX_VALUE;
            return elementCheckPeek;
        }
        long waitMills = message.getNextSendTime() - SystemClock.uptimeMillis();
        if (waitMills <= 0) {
            elementCheckPeek.message = message;
            elementCheckPeek.waitMills = 0;
            list.removeFirst();
        } else {
            elementCheckPeek.message = null;
            elementCheckPeek.waitMills = waitMills;
        }
        return elementCheckPeek;
    }

    /**
     * 开启线程
     * 仅仅执行一次即可
     */
    protected void startThread() {
        new Thread(() -> {
            while (running) {
                ElementCheckPeek checkPeek = checkAndPoll();
                if (checkPeek.message != null) {
                    onNextMessage(checkPeek.message);
                    continue;
                }
                synchronized (waitObj) {
                    try {
                        waitObj.wait(checkPeek.waitMills);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!running) {
                    break;
                }
            }
        }).start();
    }

    /**
     * 下一个消息发送的时间到了，通知上层怎么处理
     *
     * @param messageWrapper 下次发送的消息
     */
    protected abstract void onNextMessage(SendMessageWrapper messageWrapper);

    /**
     * 简单的封装
     */
    private class ElementCheckPeek {
        /**
         * 返回的消息。null表示不需要发送下一个消息了
         */
        SendMessageWrapper message;
        /**
         * 下个等待的时间。
         * 仅仅 message==null有效，表示等待多长时间
         * message!=null时，始终为0
         */
        long waitMills;
    }
}
