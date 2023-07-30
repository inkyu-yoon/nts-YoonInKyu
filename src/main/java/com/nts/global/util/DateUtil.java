package com.nts.global.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static String convertLocalDateTimeToString(LocalDateTime localDateTime) {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Timestamp.valueOf(localDateTime));
    }

    public static boolean checkRecentDate(LocalDateTime createdDate) {
        // 현재 시간 구하기
        LocalDateTime now = LocalDateTime.now();

        // 최근 3일 이내인지 확인
        long daysDifference = ChronoUnit.DAYS.between(createdDate, now);
        if (daysDifference <= 3) {
            return true;
        } else {
            return false;
        }
    }
}
