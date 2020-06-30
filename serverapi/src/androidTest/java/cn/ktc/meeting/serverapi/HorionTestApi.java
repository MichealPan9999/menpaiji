package cn.ktc.meeting.serverapi;


import cn.ktc.jkf.serverapi.request.BaseRequestApi;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 测试 https://hwrite.horion.com/doc/genDocId?docId=5bf37c53ba9aa
 */
public class HorionTestApi extends BaseRequestApi {
    private LoginApiStore mApiStore;

    public HorionTestApi() {
        super("https://hwrite.horion.com");
        mApiStore = mRetrofit.create(LoginApiStore.class);
    }

    /**
     * 执行登录操作
     *
     * @param tokenId
     */
    public Observable<HorionTestResult> login(String tokenId) {
        return mApiStore.login(tokenId);
    }

    public interface LoginApiStore {
        @GET("/doc/genDocId")
        Observable<HorionTestResult> login(@Query("docId") String docId);
    }
}
