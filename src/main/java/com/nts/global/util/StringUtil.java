package com.nts.global.util;

import java.util.List;

public class StringUtil {
    public static String convertListToString(List<String> list) {
        StringBuilder sb = new StringBuilder();

        if (!list.isEmpty()) {
            for (String hashtag : list) {
                sb.append(String.format("#%s ", hashtag));
            }
        }
        return sb.toString();
    }

}
