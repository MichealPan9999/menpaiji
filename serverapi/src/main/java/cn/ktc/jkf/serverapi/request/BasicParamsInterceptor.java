package cn.ktc.jkf.serverapi.request;

import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.ktc.jkf.utils.EnvironmentInfo;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 使用okhtt请求时默认的拦截器，用于在请求时增加默认的参数
 * 例如：os, hardward, ...
 */

/**
 * @author hq
 * @hide
 **/
public class BasicParamsInterceptor implements Interceptor {

    private Map<String, String> queryParamsMap = new HashMap<>();
    private Map<String, String> paramsMap = new HashMap<>();
    private Map<String, String> headerParamsMap = new HashMap<>();
    private List<String> headerLinesList = new ArrayList<>();

    private BasicParamsInterceptor() {

    }

    public static BasicParamsInterceptor getDefault() {
        BasicParamsInterceptor instance = new BasicParamsInterceptor();
        instance.addQueryParam(EParamKey.OS.getName(), EnvironmentInfo.getOSName());
        instance.addQueryParam(EParamKey.OSVER.getName(), EnvironmentInfo.getOSVersion());
        //instance.addQueryParam(EParamKey.DEVICEID.getName(), EnvironmentInfo.getDeviceId());
        instance.addQueryParam(EParamKey.LANGUAGE.getName(), EnvironmentInfo.getLanguage());
        instance.addQueryParam(EParamKey.HW.getName(), EnvironmentInfo.getHardware());
        instance.addQueryParam(EParamKey.APPNAME.getName(), EnvironmentInfo.getAppName());
        if(!TextUtils.isEmpty(EnvironmentInfo.getInitReqId())) {
            instance.addQueryParam(EParamKey.REQID.getName(), EnvironmentInfo.getInitReqId());
        }
        if (EnvironmentInfo.getUID() >= 0) {
            instance.addQueryParam(EParamKey.UID.getName(), String.valueOf(EnvironmentInfo.getUID()));
        }
        instance.addQueryParam(EParamKey.UTOKEN.getName(), EnvironmentInfo.getUToken());
        return instance;
    }

    /**
     * 增加到普通的HTTP GET URL的参数
     */
    public BasicParamsInterceptor addQueryParam(String key, String value) {
        if (TextUtils.isEmpty(value)) {
            queryParamsMap.remove(key);
        } else {
            queryParamsMap.put(key, value);
        }
        return this;
    }

    public BasicParamsInterceptor removeQueryParam(String key) {
        queryParamsMap.remove(key);
        return this;
    }

    /**
     * 增加到POST BODY中的参数
     */
    public BasicParamsInterceptor addPostParam(String key, String value) {
        if (TextUtils.isEmpty(value)) {
            paramsMap.remove(key);
        } else {
            paramsMap.put(key, value);
        }
        return this;
    }

    public BasicParamsInterceptor removePostParam(String key) {
        paramsMap.remove(key);
        return this;
    }

    /**
     * 增加到请求Heads中
     */
    public BasicParamsInterceptor addHeadParam(String key, String value) {
        if (TextUtils.isEmpty(value)) {
            headerParamsMap.remove(key);
        } else {
            headerParamsMap.put(key, value);
        }
        return this;
    }

    public BasicParamsInterceptor removeHeadParam(String key) {
        headerParamsMap.remove(key);
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();

        // process header params inject
        if (headerParamsMap != null && headerParamsMap.size() > 0) {
            Set<String> keys = headerParamsMap.keySet();
            for (String headerKey : keys) {
                requestBuilder.addHeader(headerKey, headerParamsMap.get(headerKey)).build();
            }
        }

        if (headerLinesList.size() > 0) {
            Headers.Builder headerBuilder = request.headers().newBuilder();
            for (String line : headerLinesList) {
                headerBuilder.add(line);
            }
            requestBuilder.headers(headerBuilder.build());
        }
        // process header params end

        // process queryParams inject whatever it's GET or POST
        if (queryParamsMap.size() > 0) {
            request = injectParamsIntoUrl(request.url().newBuilder(), requestBuilder, queryParamsMap);
        }

        // process post body inject
        if (paramsMap.size() > 0) {
            if (canInjectIntoBody(request)) {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                    formBodyBuilder.add((String) entry.getKey(), (String) entry.getValue());
                }

                RequestBody formBody = formBodyBuilder.build();
                String postBodyString = bodyToString(request.body());
                postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
                requestBuilder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString));
            }
        }

        request = requestBuilder.build();
        return chain.proceed(request);
    }

    private boolean canInjectIntoBody(Request request) {
        if (request == null) {
            return false;
        }
        if (!TextUtils.equals(request.method(), "POST")) {
            return false;
        }
        RequestBody body = request.body();
        if (body == null) {
            return false;
        }
        MediaType mediaType = body.contentType();
        if (mediaType == null) {
            return false;
        }
        if (!TextUtils.equals(mediaType.subtype(), "x-www-form-urlencoded")) {
            return false;
        }
        return true;
    }

    // func to inject params into url
    private Request injectParamsIntoUrl(HttpUrl.Builder httpUrlBuilder, Request.Builder requestBuilder, Map<String, String> paramsMap) {
        if (paramsMap.size() > 0) {
            Map<String, String> alreadyParams = urlToParams(httpUrlBuilder.toString(), true);
            Iterator iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                if (alreadyParams.containsKey(key.toLowerCase())) {
                    continue;
                }
                httpUrlBuilder.addQueryParameter((String) entry.getKey(), (String) entry.getValue());
            }
            requestBuilder.url(httpUrlBuilder.build());
            return requestBuilder.build();
        }

        return null;
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null) {
                copy.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    /**
     * 将一个URL字符串取出所有的参数键值对
     * 例如：http://127.0.0.1:8080/login?ac=init&hw=tv2 取出为 (ac,init), (hw, tv2)
     *
     * @param url       需要计算的URL地址
     * @param lowerCase 是否KEY转换成小写
     * @return 返回Map
     */
    private static Map<String, String> urlToParams(String url, boolean lowerCase) {
        Map<String, String> params = new HashMap<>();
        int pos = url.indexOf('?');
        String s = pos >= 0 ? url.substring(pos + 1) : url;
        String[] ss = s.split("&");
        for (String p : ss) {
            String[] kv = p.split("=");
            if (kv.length != 2) {
                continue;
            }
            params.put(lowerCase ? kv[0].toLowerCase() : kv[0], kv[1]);
        }
        return params;
    }

    public static class Builder {

        BasicParamsInterceptor interceptor;

        public Builder() {
            interceptor = new BasicParamsInterceptor();
        }

        public Builder addParam(String key, String value) {
            interceptor.paramsMap.put(key, value);
            return this;
        }

        public Builder addParamsMap(Map<String, String> paramsMap) {
            interceptor.paramsMap.putAll(paramsMap);
            return this;
        }

        public Builder addHeaderParam(String key, String value) {
            interceptor.headerParamsMap.put(key, value);
            return this;
        }

        public Builder addHeaderParamsMap(Map<String, String> headerParamsMap) {
            interceptor.headerParamsMap.putAll(headerParamsMap);
            return this;
        }

        public Builder addHeaderLine(String headerLine) {
            int index = headerLine.indexOf(":");
            if (index == -1) {
                throw new IllegalArgumentException("Unexpected header: " + headerLine);
            }
            interceptor.headerLinesList.add(headerLine);
            return this;
        }

        public Builder addHeaderLinesList(List<String> headerLinesList) {
            for (String headerLine : headerLinesList) {
                int index = headerLine.indexOf(":");
                if (index == -1) {
                    throw new IllegalArgumentException("Unexpected header: " + headerLine);
                }
                interceptor.headerLinesList.add(headerLine);
            }
            return this;
        }

        public Builder addQueryParam(String key, String value) {
            interceptor.queryParamsMap.put(key, value);
            return this;
        }

        public Builder addQueryParamsMap(Map<String, String> queryParamsMap) {
            interceptor.queryParamsMap.putAll(queryParamsMap);
            return this;
        }

        public BasicParamsInterceptor build() {
            return interceptor;
        }

    }
}
