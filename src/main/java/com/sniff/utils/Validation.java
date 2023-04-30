package com.sniff.utils;

public class Validation {
    public static boolean isValidPhone(String phone) {
        return phone.matches("^\\+380\\d{9}$");
    }
}
