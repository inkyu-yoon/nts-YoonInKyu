package com.nts.global.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class DateUtil {

    public static String convertLocalDateTimeToString(LocalDateTime localDateTime) {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Timestamp.valueOf(localDateTime));
    }
}
