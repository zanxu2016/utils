package me.xuzan.utils.comm;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommTool {

    private static final String DATE_PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";

    //格式化时间
    public static String getDateStr(Date date) {
        return new SimpleDateFormat(DATE_PATTERN_FULL).format(date);
    }
}
