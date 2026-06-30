package util;

import java.util.concurrent.ConcurrentHashMap;

public class MemoryCacheStore {
    // Lưu mã giao dịch Webhook (VD: "TEA12345" -> true)
    public static final ConcurrentHashMap<String, Boolean> transactions = new ConcurrentHashMap<>();

    // Đếm số lần đăng nhập sai của user (Chống Brute-force)
    public static final ConcurrentHashMap<String, Integer> failedAttempts = new ConcurrentHashMap<>();

    // Lưu thời gian khóa tài khoản (Unix timestamp)
    public static final ConcurrentHashMap<String, Long> lockTimeouts = new ConcurrentHashMap<>();
}