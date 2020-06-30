package cn.ktc.jkf.utils;

import android.annotation.SuppressLint;
import android.os.SystemClock;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 对RxJava的一些封装
 *
 * @author hq
 */
public class RxHelper {

    /**
     * 使用Rx线程功能的回调执行
     *
     * @param <T> param的类型
     */
    @FunctionalInterface
    public interface Runnable<T> {
        void run(T param);
    }

    /**
     * 一个简单的封装，在io线程中处理
     */
    public static Observable<Long> getObservable_io() {
        return Observable.just(SystemClock.uptimeMillis())
                .compose(RxSchedulersHelper.io_io());
    }

    /**
     * 使用Rx的线程切换功能，IO线程执行
     *
     * @param runnable 执行体
     * @param param    参数
     * @param <T>      参数类型
     */
    @SuppressLint("CheckResult")
    public static <T> void excuteIoThread(Runnable<T> runnable, T param) {
        Observable.just(SystemClock.uptimeMillis())
                .compose(RxSchedulersHelper.io_io())
                .subscribe(result -> runnable.run(param));
    }

    /**
     * 使用Rx的线程切换功能，主线程执行
     *
     * @param runnable 执行体
     * @param param    参数
     * @param <T>      参数类型
     */
    @SuppressLint("CheckResult")
    public static <T> void excuteMainThread(Runnable<T> runnable, T param) {
        Observable.just(SystemClock.uptimeMillis())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> runnable.run(param));
    }

    /**
     * 调度封装
     */
    public static class RxSchedulersHelper {

        /**
         * 网络请求的封装，
         * 网络请求：Schedulers.io()
         * 处理：AndroidSchedulers.mainThread()
         */
        public static <T> ObservableTransformer<T, T> io_main() {
            return upstream -> upstream
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

        /**
         * 网络请求的封装
         * 网络请求：Schedulers.io()
         * 处理：Schedulers.io()
         */
        public static <T> ObservableTransformer<T, T> io_io() {
            return upstream -> upstream
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io());
        }
    }
}
