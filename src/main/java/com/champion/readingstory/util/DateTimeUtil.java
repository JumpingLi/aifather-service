package com.champion.readingstory.util;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 18:17
 */
public class DateTimeUtil {
    public static String formatLocalDate(Timestamp timestamp){
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
