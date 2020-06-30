package cn.ktc.jkf.utils.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import cn.ktc.jkf.utils.EnvironmentInfo;

/**
 * 持久化存储工具类
 * 对于外界来说，只需要调用接口，不关心是使用SP还是SQLITE
 *
 * @author hq
 */
public class PersistUtil {
    private static final WeakHashMap<Operator, String> maps = new WeakHashMap<>();

    /**
     * 获取写入字符串操作符
     *
     * @param dbName 文件（或表）名称，分类名称
     * @return {@link IStringReader}
     */
    public static IStringWriter getStringWriter(String dbName) {
        return getOperator(dbName);
    }

    /**
     * 获取读取字符串操作符
     *
     * @param dbName 文件（或表）名称，分类名称
     * @return {@link IStringWriter}
     */
    public static IStringReader getStringReader(String dbName) {
        return getOperator(dbName);
    }

    /**
     * 保存字符串内容。简单的封装
     *
     * @param dbName 文件名
     * @param key    键值
     * @param value  内容
     */
    public static void save(String dbName, String key, String value) {
        getOperator(dbName).save(key, value);
    }

    /**
     * 读取字符串内容。简单的封装
     *
     * @param dbName   文件名
     * @param key      键值
     * @param defValue 默认内容
     * @return 读取到的字符串。如果该键值无内容，则返回defValue
     */
    public static String get(String dbName, String key, String defValue) {
        return getOperator(dbName).read(key, defValue);
    }

    private static Operator getOperator(String dbName) {
        synchronized (maps) {
            Set<Map.Entry<Operator, String>> entrySet = maps.entrySet();
            for (Map.Entry<Operator, String> entry : entrySet) {
                if (entry.getValue().equals(dbName)) {
                    return entry.getKey();
                }
            }

            Operator operator = new Operator(dbName);
            maps.put(operator, dbName);
            return operator;
        }
    }

    private static class Operator implements IStringWriter, IStringReader {
        final String dbName;
        final SharedPreferences sp;

        Operator(String dbName) {
            this.dbName = dbName;
            this.sp = EnvironmentInfo.getApplicationContext().getSharedPreferences(dbName, Context.MODE_PRIVATE);
        }

        @Override
        public String read(String key, String defValue) {
            return sp.getString(key, defValue);
        }

        @Override
        public boolean save(String key, String value) {
            sp.edit().putString(key, value).apply();
            return true;
        }
    }
}
