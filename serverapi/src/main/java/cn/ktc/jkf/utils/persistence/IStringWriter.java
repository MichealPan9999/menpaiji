package cn.ktc.jkf.utils.persistence;

/**
 * 数据持久化操作之数据保存
 * <p>
 * 将String内容保存到指定键
 *
 * @author hq
 */
public interface IStringWriter {
    /**
     * 保存字符串
     *
     * @param key   key
     * @param value 需要保存的内容
     * @return 是否保存成功
     */
    boolean save(String key, String value);
}
