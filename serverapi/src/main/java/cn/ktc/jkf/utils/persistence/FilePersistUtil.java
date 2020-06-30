package cn.ktc.jkf.utils.persistence;

import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import cn.ktc.jkf.serverapi.data.json.JsonHelper;

/**
 * 通过使用文件共享的方式实现跨进程间的调用
 * 1.文件格式为JSON字符串，利用GSON可以快速的解析
 * 2.文件中必须要包括 FILEMAGIC:"cn.ktc.jkf:db"，用于标记文件
 * <p>
 * 实现原理：
 * 1.打开文件，并且使用FileLock锁住文件
 * 2.将所有的内容解析到一个HashMap中
 * 3.读取时，从这个HashMap获取
 * 4.写入时，保存到这个HashMap中
 * 5.close()时，如果执行了写入操作，则保存到文件；否则直接关闭文件
 * 6.关闭文件前，需要解除文件锁定
 * <p>
 * 异常处理：
 * 如果文件打不开（打开时异常），则所有的操作都是无效的，
 * 但App中察觉不到，这样做是便于App直接调用。
 * 通过save()如果返回false可以判断是否处于无效状态
 *
 * @author hq
 */
public class FilePersistUtil {
    /**
     * 文件必须包括，表明是跨进程共享文件
     */
    private static final String MAGIC_KEY = "FILEMAGIC";
    private static final String MAGIC_VALUE = "cn.ktc.jkf:db";
    /**
     * 定义默认的文件名
     */
    private static final File DEFAULT_DB = new File(Environment.getExternalStorageDirectory(), ".jkf.db");

    /**
     * 获取一个进程共享的存储器
     * 注意：由于共享问题，可能或阻塞（等待其他进程操作完成）
     *
     * @param dbName 文件名称。名称一致时可以跨进程共享。默认设置为null即可
     * @return 操作符
     */
    public static IFilePersist open(String dbName) {
        File file = TextUtils.isEmpty(dbName) ? DEFAULT_DB : new File(dbName);
        return new Operator(file);
    }

    private static class Operator implements IFilePersist {
        final File file;
        RandomAccessFile accessFile = null;
        FileChannel fileChannel = null;
        FileLock lock = null;
        /**
         * 是否执行了sae()操作，如果是则在close()时需要写入到文件，否则不写入文件
         */
        boolean saved = false;
        /**
         * 保存了的所有键值对
         */
        private HashMap<String, String> params;

        private Operator(File file) {
            this.file = file;
            try {
                //打开文件（不存在时创建）。如果失败，后续所有操作都是无效的
                accessFile = new RandomAccessFile(file, "rws");
                fileChannel = accessFile.getChannel();
                while (true) {
                    try {
                        lock = fileChannel.tryLock();
                    } catch (Exception ignored) {

                    }
                    if (lock == null) {
                        SystemClock.sleep(5);
                        continue;
                    }
                    break;
                }
                long size = accessFile.length();
                byte[] content = new byte[(int) size];
                accessFile.read(content);
                params = JsonHelper.fromJson(new String(content), new TypeToken<HashMap<String, String>>() {
                }.getType());
                if (params == null || !MAGIC_VALUE.equals(params.get(MAGIC_KEY))) {
                    params = null;
                }
            } catch (IOException ignored) {
            }
            if (params == null) {
                params = new HashMap<>();
            }
        }

        @Override
        public void close() {
            try {
                if (saved) {
                    params.put(MAGIC_KEY, MAGIC_VALUE);
                    String json = JsonHelper.toJson(params);
                    if (json != null && accessFile != null) {
                        try {
                            accessFile.setLength(0);
                            accessFile.write(json.getBytes(StandardCharsets.UTF_8));
                        } catch (Exception ignored) {

                        }
                    }
                }
                if (lock != null) {
                    lock.release();
                    lock.close();
                }
                if (fileChannel != null) {
                    fileChannel.close();
                }
                if (accessFile != null) {
                    accessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String read(String key, String defValue) {
            String value = params.get(key);
            return value == null ? defValue : value;
        }

        @Override
        public boolean save(String key, String value) {
            if (accessFile == null) {
                return false;
            }
            if (value == null) {
                params.remove(key);
            } else {
                params.put(key, value);
            }
            saved = true;
            return true;
        }
    }

}
