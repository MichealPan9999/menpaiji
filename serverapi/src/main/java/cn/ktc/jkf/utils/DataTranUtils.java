package cn.ktc.jkf.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataTranUtils {
    /**
     * 时间戳转时间格式
     * 
     * @param longTime
     * @return
     * @throws ParseException
     */
    public static String longToDate(Object longTime) throws ParseException {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(longTime + "")));
    }
}
