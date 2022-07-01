package com.github.ivanfomkin.bookshop.util;

import java.util.random.RandomGenerator;

public class CommonUtils {
    private CommonUtils() {
    }

    private static final RandomGenerator randomGenerator = RandomGenerator.getDefault();
    private static final int CODE_LENGTH = 4;

    public static String formatPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("\\D", "");
    }

    public static String generateRandomCode() {
        var builder = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            builder.append(randomGenerator.nextInt(0, 10));
        }
        return builder.toString();
    }

    public static boolean isPhoneNumber(String contact) {
        return !contact.contains("@");
    }
}
