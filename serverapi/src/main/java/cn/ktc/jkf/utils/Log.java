package cn.ktc.jkf.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cn.ktc.jkf.serverapi.data.json.BaseJsonBean;

/**
 * 自定义的日志，允许设置开关是否打印
 *
 * @author hq
 */
public class Log {
    /**
     * WebSocket
     */
    private static final String TAG_WS = "WS";

    /**
     * WebSocket发送
     */
    private static final String TAG_WSS = "WSS";
    /**
     * WebSocket接收
     */
    private static final String TAG_WSR = "WSR";
    /**
     * HTTP
     */
    private static final String TAG_HTTP = "HTTP";

    public static boolean WSR = true;
    public static boolean WSS = true;
    public static boolean HTTP = true;


    /**
     * 格式化输出（缩进）的方式，一般用于调试
     */
    private static Gson gson_pretty = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static int ws(Object... msgs) {
        if (WSR || WSS) {
            return log(TAG_WS, msgs);
        }
        return 0;
    }

    public static int wss(Object... msgs) {
        return WSS ? log(TAG_WSS, msgs) : 0;
    }

    public static int wsr(Object... msgs) {
        return WSR ? log(TAG_WSR, msgs) : 0;
    }

    public static int http(Object... msgs) {
        return HTTP ? log(TAG_HTTP, msgs) : 0;
    }

    /**
     * 输出日志
     *
     * @param tag  TAG
     * @param msgs 需要输出的消息列表
     * @return android.util.Log返回值
     */
    public static int log(String tag, Object... msgs) {
        if (msgs == null) {
            return 0;
        }
        if (msgs.length == 1) {
            if ((msgs[0] instanceof BaseJsonBean) || (msgs[0] instanceof JsonObject)) {
                return android.util.Log.i(tag, toJson(msgs[0]));
            } else {
                return android.util.Log.i(tag, String.valueOf(msgs[0]));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Object m : msgs) {
            if ((m instanceof BaseJsonBean) || (m instanceof JsonObject)) {
                sb.append("\n");
                sb.append(toJson(m));
            } else {
                sb.append(String.valueOf(m));
            }
        }
        return android.util.Log.i(tag, sb.toString());
    }

    private static String toJson(Object object) {
        try {
            return gson_pretty.toJson(object);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
