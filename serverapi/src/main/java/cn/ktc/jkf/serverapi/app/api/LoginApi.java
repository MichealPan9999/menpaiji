package cn.ktc.jkf.serverapi.app.api;

import cn.ktc.jkf.serverapi.ServerApiException;
import cn.ktc.jkf.serverapi.data.json.BaseJsonBean;
import cn.ktc.jkf.serverapi.data.json.result.BindResult;
import cn.ktc.jkf.serverapi.data.json.result.InitResult;
import cn.ktc.jkf.serverapi.data.json.websocket.message.SyncResult;
import cn.ktc.jkf.serverapi.request.BaseRequestApi;
import cn.ktc.jkf.serverapi.utils.SystemUtil;
import cn.ktc.jkf.utils.EnvironmentInfo;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class LoginApi extends BaseRequestApi {
    private LoginApiStore mApiStore;

    /**
     * 新建实例
     *
     * @param host 后台服务器地址（包括端口号），例如：http://127.0.0.1:8080
     */
    public LoginApi(String host) {
        super(host);
        mApiStore = mRetrofit.create(LoginApiStore.class);
    }

    /**
     * App启动时获取初始化信息(Rx调用)
     *
     * @return Rx的Observable
     */
    public Observable<InitResult> init() {
        String deviceId = EnvironmentInfo.getDeviceId();
        return mApiStore.init(deviceId)
                .doOnNext(initResult -> {
                    if (!initResult.isSuccessful()) {
                        throw LoginApiException.init(initResult);
                    }
                });
    }

    /**
     * 绑定设备
     *
     * @param number 设备号
     * @param count  最大容纳人数
     * @return
     */
    public Observable<BindResult> bindDevice(String number, int count) {
        String deviceId = EnvironmentInfo.getDeviceId();
        String tvModel = SystemUtil.getSystemModel();
        String tvSystem = SystemUtil.getSystemVersion();
        return mApiStore.binddevice(deviceId, number, String.valueOf(count), tvModel,tvSystem)
                .doOnNext(result -> {
                    if (!result.isSuccessful()) {
                        throw LoginApiException.bind(result);
                    }
                });
    }

    /**
     * 同步后台消息
     * 根据版本号同步后台消息，取得该版本号以后的消息内容
     *
     * @param version 当前保存的版本号
     * @return
     */
    public Observable<SyncResult> syncMessages(int version) {
        String deviceId = EnvironmentInfo.getDeviceId();
        String tvModel = SystemUtil.getSystemModel();
        return mApiStore.sync(deviceId, version)
                .doOnNext(result -> {
                    if (!result.isSuccessful()) {
                        throw LoginApiException.sync(result);
                    }
                });
    }

    private interface LoginApiStore {
        /**
         * 获取初始化信息，包括reqid, wsaddr, wsport等{@link InitResult}
         *
         * @return Rx的Observable
         */
        @GET("login/init?")
        Observable<InitResult> init(@Query("deviceid") String deviceId);

        /**
         * 绑定设备
         */
        @GET("login/binddevice?")
        Observable<BindResult> binddevice(@Query("deviceid") String deviceid, @Query("roomnum") String number, @Query("count") String count, @Query("model") String model,@Query("tvsystem") String tvSystem);

        /**
         * 同步后台信息
         */
        @GET("tv/sync?")
        Observable<SyncResult> sync(@Query("deviceid") String deviceid, @Query("ctrlversion") int version);
    }

    /**
     * LoginApi的异常基类
     */
    public static class LoginApiException extends ServerApiException {
        final static int ID_BIND = IDEXCEPTION_LOGINAPI + 1;
        final static int ID_INIT = IDEXCEPTION_LOGINAPI + 2;
        final static int ID_SYNC = IDEXCEPTION_LOGINAPI + 3;

        private LoginApiException(BaseJsonBean jsonBean, int id) {
            super(jsonBean, id);
        }

        public static LoginApiException bind(BindResult result) {
            return new LoginApiException(result, ID_BIND);
        }

        public static LoginApiException init(InitResult result) {
            return new LoginApiException(result, ID_INIT);
        }

        public static LoginApiException sync(SyncResult result) {
            return new LoginApiException(result, ID_SYNC);
        }

        public boolean isInit() {
            return idException == ID_BIND;
        }

        public boolean isBind() {
            return idException == ID_INIT;
        }

        public boolean isSync() {
            return idException == ID_SYNC;
        }

    }

}
