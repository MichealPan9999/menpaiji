package cn.ktc.jkf.serverapi.websocket;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;

import cn.ktc.jkf.utils.RxHelper;


/**
 * 对App注册的消息统一管理
 *
 * @author hq
 */
public abstract class MessageListenerManager<T> {
    @FunctionalInterface
    protected interface Runnable<D> {
        void run(Data<D> data);
    }

    protected final ArrayList<Data> list = new ArrayList<>();

    /**
     * 加入消息监听
     */
    public void add(int msgType, T listener, boolean mainThread, String group) {
        if (listener == null) {
            return;
        }
        synchronized (this) {
            for (Data data : list) {
                if (data.msgType == msgType && data.listener == listener) {
                    data.mainThread = mainThread;
                    data.group = group;
                    return;
                }
            }
            Data<T> data = new Data<>();
            data.msgType = msgType;
            data.listener = listener;
            data.mainThread = mainThread;
            data.group = group;
            list.add(data);
        }
    }

    /**
     * 移除监听器
     *
     * @param msgType  消息类型。0表示忽略，只需要比对 listener
     * @param listener 监听器
     */
    public void remove(int msgType, T listener) {
        synchronized (this) {
            Iterator<Data> iterator = list.iterator();
            while (iterator.hasNext()) {
                Data data = iterator.next();
                if ((data.listener == listener) && (msgType == 0 || msgType == data.msgType)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * 移除分组的
     *
     * @param group 分组名称。null表示什么都不做
     */
    public synchronized void remove(String group) {
        if (TextUtils.isEmpty(group)) {
            return;
        }
        Iterator<Data> iterator = list.iterator();
        while (iterator.hasNext()) {
            Data data = iterator.next();
            if (group.equals(data.group)) {
                iterator.remove();
            }
        }
    }

    /**
     * 清空所有的，在App退出时调用，避免内存泄漏
     */
    public synchronized void clear() {
        list.clear();
    }

    public synchronized int size() {
        return list.size();
    }

    /**
     * 循环执行所有的
     */
    protected synchronized <D> void excuteAll(Runnable<D> run) {
        for (Data data : list) {
            if (data.mainThread) {
                RxHelper.excuteMainThread((param -> {
                    run.run(data);
                }), null);
            } else {
                RxHelper.excuteIoThread((param -> {
                    run.run(data);
                }), null);

            }
        }
    }

    protected static class Data<D> {
        /**
         * 分组的名称
         */
        String group;
        /**
         * 注册消息类型
         */
        public int msgType;
        /**
         * 监听回调
         */
        public D listener;
        /**
         * 是否在主线程中回调
         */
        public boolean mainThread;
    }
}
