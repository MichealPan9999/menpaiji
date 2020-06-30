package cn.ktc.jkf.serverapi.app.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import cn.ktc.jkf.serverapi.app.api.LoginApi;
import cn.ktc.jkf.serverapi.data.json.JsonHelper;
import cn.ktc.jkf.utils.RxHelper;
import io.reactivex.Observable;

import cn.ktc.jkf.serverapi.data.json.result.BindResult;
import cn.ktc.jkf.serverapi.websocket.WebSocketApi;

public class LoginHelper {

    /**
     * 调用LoginApi.init()成功后，执行连接WebSocket
     *
     */
    public static void doConnectWebSocket(Context context, String wsAddr) throws Exception {
        WebSocketApi.getInstance().setAppContext(context.getApplicationContext());
        WebSocketApi.getInstance().setInfo(wsAddr).connectWebSocket();
    }

    public static void disConnectWebSocket()throws Exception{
        WebSocketApi.getInstance().closeWebSocket();
    }

    /**
     * 绑定设备
     * @param context   上下文
     * @param serverIp  服务器IP，通过该服务器IP将设备绑定到服务器
     * @param number    设备会议室号
     * @param count     会议室最大容纳人数
     * @return
     */
    @SuppressLint("CheckResult")
    public static Observable<BindResult> doBindDevice(Context context,String serverIp,String number,int count){
        LoginApi loginApi = new LoginApi(serverIp);
        Observable<BindResult> bindResultObservable = null;
        bindResultObservable = loginApi.bindDevice(number,count);

        return bindResultObservable
                .compose(RxHelper.RxSchedulersHelper.io_io())
                .doOnNext(result ->{
                    if (result.isSuccessful())
                    {
                        //绑定成功
                    }
                });
    }
}
