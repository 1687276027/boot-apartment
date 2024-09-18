package com.atguigu.lease.common.utils;

import java.util.Random;

public class CodeUtil {

    public static String getRandomCode(Integer lenth) {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < lenth; i++) {
            int num = r.nextInt(10);
            sb.append(num);
        }
        return sb.toString();
    }
}
