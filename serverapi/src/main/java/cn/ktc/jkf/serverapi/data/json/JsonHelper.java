package cn.ktc.jkf.serverapi.data.json;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * 使用GSON做序列化（反序列化）操作， 用于辅助操作，避免每次都 new GSon()
 *
 * @author hq
 */
public class JsonHelper {
    /**
     * 创建一个默认的GSon对象
     */
    private static Gson gson = new GsonBuilder()
            // .setPrettyPrinting()
            .disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();

    private static JsonParser jsonParser = new JsonParser();

    /**
     * 从一个JSON字符串中反序列化出对象
     *
     * @param json     JSON字符串
     * @param classOfT 类名
     * @return 成功返回实际的对象。失败返回null
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 指定Type格式化，主要是针对List<T>这种泛型的情况
     *
     * @param json    JSON字符串
     * @param typeOfT Type类型，例如：new TypeToken<List<String>>() {}.getType()
     * @return 成功返回实际的对象。失败返回null
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return gson.fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson(Object object) {
        try {
            return gson.toJson(object);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonObject toJsonTree(Object object) {
        JsonElement jsonElement = gson.toJsonTree(object);
        return jsonElement instanceof JsonObject ? (JsonObject) jsonElement : null;
    }

    public static <T> T fromJsonObject(JsonElement jsonElement, Class<T> classOfT) {
        try {
            return gson.fromJson(jsonElement, classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJsonObject(JsonElement jsonElement, Type typeOfT) {
        try {
            return gson.fromJson(jsonElement, typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonElement fromJavaBean(Object object) {
        String json = toJson(object);
        if (json == null) {
            return null;
        }
        return jsonParser.parse(json);
    }

    /**
     * 从一个字符串序列化出Json对象
     *
     * @param text 传入字符串
     * @return 成功返回实际的对象，失败返回一个新的JsonObject
     */
    public static JsonObject fromString(String text) {
        if (text == null || jsonParser.parse(text) == null) {
            return new JsonObject();
        }
        return jsonParser.parse(text).getAsJsonObject();
    }

    public static JsonObject fromString2(String text) {
        if (text == null || jsonParser.parse(text) == null) {
            return null;
        }
        try {
            return jsonParser.parse(text).getAsJsonObject();
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 从一个字符串序列化出Json数组
     *
     * @param text 传入字符串
     * @return 成功返回实际的对象，失败返回一个新的JsonArray
     */
    public static JsonArray fromStringToArray(String text) {
        if (text == null || jsonParser.parse(text) == null) {
            return new JsonArray();
        }
        return jsonParser.parse(text).getAsJsonArray();
    }

    /**
     * 从JsonObject中取出对应String value
     *
     * @param jsonObject 操作的Json
     * @param key        需要取出value的key
     * @param defValue   默认值
     * @return 成功返回实际的String，失败返回默认值
     */
    public static String getJsonValue(JsonObject jsonObject, String key, String defValue) {
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null || (jsonElement instanceof JsonNull)) {
            return defValue;
        }
        return jsonElement.getAsString();
    }

    /**
     * 从JsonObject中取出对应int value
     *
     * @param jsonObject 操作的Json
     * @param key        需要取出value的key
     * @param defValue   默认值
     * @return 成功返回实际的int，失败返回默认值
     */
    public static int getJsonValue(JsonObject jsonObject, String key, int defValue) {
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            return defValue;
        }
        return jsonElement.getAsInt();
    }

    /**
     * 从JsonObject中取出对应float value
     *
     * @param jsonObject 操作的Json
     * @param key        需要取出value的key
     * @param defValue   默认值
     * @return 成功返回实际的float，失败返回默认值
     */
    public static float getJsonValue(JsonObject jsonObject, String key, float defValue) {
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            return defValue;
        }
        return jsonElement.getAsFloat();
    }

    /**
     * 从JsonObject中取出对应long value
     *
     * @param jsonObject 操作的Json
     * @param key        需要取出value的key
     * @param defValue   默认值
     * @return 成功返回实际的long，失败返回默认值
     */
    public static long getJsonValue(JsonObject jsonObject, String key, long defValue) {
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            return defValue;
        }
        return jsonElement.getAsLong();
    }

    /**
     * 从JsonObject中取出对应boolean value
     *
     * @param jsonObject 操作的Json
     * @param key        需要取出value的key
     * @param defValue   默认值
     * @return 成功返回实际的boolean，失败返回默认值
     */
    public static boolean getJsonValue(JsonObject jsonObject, String key, boolean defValue) {
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            return defValue;
        }
        return jsonElement.getAsBoolean();
    }

    /**
     * 从JsonObject中取出对应JsonObject value
     *
     * @param jsonObject 操作的Json
     * @param key        需要取出value的key
     * @param defValue   默认值
     * @return 成功返回实际的JsonObject，失败返回默认值
     */
    public static JsonObject getJsonValue(JsonObject jsonObject, String key, JsonObject defValue) {
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            return defValue;
        }
        return jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : defValue;
    }

    /**
     * 从JsonObject中取出对应JsonArray value
     *
     * @param jsonObject 操作的Json
     * @param key        需要取出value的key
     * @param defValue   默认值
     * @return 成功返回实际的JsonArray，失败返回默认值
     */
    public static JsonArray getJsonValue(JsonObject jsonObject, String key, JsonArray defValue) {
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            return defValue;
        }
        return jsonElement.isJsonArray() ? jsonElement.getAsJsonArray() : defValue;
    }

    public static boolean getBoolean(JsonObject jsonObject, String name, boolean def) {
        JsonElement ele = jsonObject.get(name);
        if (ele == null || ele.isJsonNull()) {
            return def;
        }
        return ele.getAsBoolean();
    }
}
