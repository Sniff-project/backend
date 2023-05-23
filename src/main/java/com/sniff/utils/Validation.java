package com.sniff.utils;

public class Validation {
    public static boolean isValidPhone(String phone) {
        return phone.matches("^\\+380\\d{9}$");
    }

    public static boolean isValidPassword(String password) {
        return password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\?]).{8,}$");
    }
}
