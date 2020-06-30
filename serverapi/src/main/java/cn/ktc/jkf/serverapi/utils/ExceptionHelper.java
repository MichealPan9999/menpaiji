package cn.ktc.jkf.serverapi.utils;

import java.net.SocketTimeoutException;

/**
 * 用于辅助判断异常类型
 *
 * @author hq
 */
public class ExceptionHelper {
    /**
     * 判定是否是网络异常。 Rx请求网络时，如果出现异常可以判断
     */
    public static boolean isSocketTimeout(Throwable e) {
        return e instanceof SocketTimeoutException;
    }

    public static boolean isServerApiException(Throwable e) {
        // return e instanceof ServerApiException;
        return false;
    }

    public static boolean isLoginApiException(Throwable e) {
        // return e instanceof LoginApi.LoginApiException;
        return false;
    }

    public static boolean isCompanyApiException(Throwable e) {
        // return e instanceof CompanyApi.CompanyApiException;
        return false;
    }

    public static boolean isMeetingApiException(Throwable e) {
        // return e instanceof MeetingApi.MeetingApiException;
        return false;
    }

    public static boolean isPictureApiException(Throwable e) {
        // return e instanceof PictureApi.PictureApiException;
        return false;
    }
}
