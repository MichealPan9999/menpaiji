package cn.ktc.jkf.utils.persistence;

/**
 * 跨进程文件共享用到的
 * 使用完毕一定要调用close()，否则可能会导致数据丢失
 *
 * @author hq
 */
public interface IFilePersist extends IStringReader, IStringWriter {
    void close();
}
