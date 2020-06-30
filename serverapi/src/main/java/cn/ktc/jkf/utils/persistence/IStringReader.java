package cn.ktc.jkf.utils.persistence;

/**
 * 数据持久化操作之数据读取
 * <p>
 * 读取键值中的String内容
 *
 * @author hq
 */
public interface IStringReader {
    /**
     * 读取内容
     *
     * @param key Key
     * @return 获取到的内容。如果不存在则返回null
     */
    default String read(String key) {
        return read(key, null);
    }

    /**
     * 读取内容
     *
     * @param key      Key
     * @param defValue 不存在时的默认值
     * @return 获取到的内容。如果不存在则返回defValue
     */
    String read(String key, String defValue);
}
