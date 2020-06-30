package cn.ktc.jkf.serverapi.request;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import cn.ktc.jkf.utils.EnvironmentInfo;
import cn.ktc.jkf.utils.Log;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 使用Retrofit的基础封装
 */

/**
 * @author hq
 * @hide
 **/
public class BaseRequestApi {
    protected Retrofit mRetrofit;

    public BaseRequestApi() {
        this(EnvironmentInfo.getHttpServer());

    }

    public BaseRequestApi(String baseUrl) {
        /*
        //需要增加OKHTTP拦截器，后面再加入
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new OkHttpInterceptor())
                .build();
        */

        // https://medium.com/square-corner-blog/okhttp-3-13-requires-android-5-818bb78d07ce
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                .addInterceptor(BasicParamsInterceptor.getDefault());
        //是否需要打印出OKHTTP的请求和返回内容
        if (EnvironmentInfo.isDebugOkhttp()) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> {
                try {
                    String text = URLDecoder.decode(message, "utf-8");
                    Log.http(text);
                } catch (UnsupportedEncodingException e) {
                    Log.http(message);
                } catch (Exception e) {
                    //出现异常，比如：上传二进制文件
                    //不用打印出文件内容了，避免消息太多
                }
            });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        //设置超时时间
        builder.connectTimeout(6_000, TimeUnit.MILLISECONDS)
                .readTimeout(10_000, TimeUnit.MILLISECONDS)
                .writeTimeout(6_000, TimeUnit.MILLISECONDS);
        OkHttpClient client = builder.build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //RxJava支持
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();

    }

}
