package me.zhangchaozhou.timepicker.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuli on 2015/11/27.
 */
public class DateUtil {


    /**
     * 使用用户格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return
     */

    public static Date parse(String strDate, String pattern) throws ParseException {

        if (TextUtils.isEmpty(strDate)) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date = df.parse(strDate);
        return date;
    }

    /**
     * 使用用户格式格式化日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return
     */

    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }
}
