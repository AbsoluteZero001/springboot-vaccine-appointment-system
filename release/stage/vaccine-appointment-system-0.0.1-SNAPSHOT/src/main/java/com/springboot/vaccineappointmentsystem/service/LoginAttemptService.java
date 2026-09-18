package com.springboot.vaccineappointmentsystem.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long FREEZE_30S_MS = 30_000;
    private static final long FREEZE_60S_MS = 60_000;

    private final ConcurrentHashMap<String, AttemptState> attempts = new ConcurrentHashMap<>();

    /**
     * Check if the username is currently frozen.
     * @return null if allowed, or a result map if blocked.
     */
    public Map<String, Object> checkBlocked(String username) {
        AttemptState state = attempts.get(username);
        if (state == null) return null;

        if (state.freezeUntil > 0) {
            long remainingMs = state.freezeUntil - System.currentTimeMillis();
            if (remainingMs > 0) {
                return freezeResult(state, remainingMs);
            }
            state.count = 0;
            state.freezeUntil = 0;
        }
        return null;
    }

    /**
     * Record a failed login attempt.
     * @return result map with error, attempts, frozen, freezeSeconds.
     */
    public Map<String, Object> recordFailedAttempt(String username) {
        AttemptState state = attempts.computeIfAbsent(username, k -> new AttemptState());
        state.count++;

        if (state.count >= MAX_ATTEMPTS) {
            state.count = 0;
            if (state.freezeLevel == 0) {
                state.freezeLevel = 1;
                state.freezeUntil = System.currentTimeMillis() + FREEZE_30S_MS;
            } else {
                state.freezeLevel = 2;
                state.freezeUntil = System.currentTimeMillis() + FREEZE_60S_MS;
            }
            return freezeResult(state, state.freezeUntil - System.currentTimeMillis());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("error", buildAttemptMessage(state.count));
        result.put("attempts", state.count);
        result.put("maxAttempts", MAX_ATTEMPTS);
        result.put("frozen", false);
        result.put("freezeSeconds", 0);
        return result;
    }

    public void recordSuccess(String username) {
        attempts.remove(username);
    }

    private Map<String, Object> freezeResult(AttemptState state, long remainingMs) {
        long seconds = Math.max(1, (remainingMs + 999) / 1000);
        Map<String, Object> result = new HashMap<>();
        result.put("attempts", MAX_ATTEMPTS);
        result.put("maxAttempts", MAX_ATTEMPTS);
        result.put("frozen", true);
        result.put("freezeSeconds", seconds);
        result.put("freezeLevel", state.freezeLevel);
        if (state.freezeLevel == 1) {
            result.put("error", "登录失败次数过多，已临时冻结30秒，请稍后再试");
        } else {
            result.put("error", "登录失败次数过多，已进入安全限制（每5次错误冻结1分钟）");
        }
        return result;
    }

    private String buildAttemptMessage(int count) {
        int remaining = MAX_ATTEMPTS - count;
        switch (remaining) {
            case 4:
                return "登录失败，请检查用户名或密码";
            case 3:
                return "已连续失败2次，请谨慎输入（5次错误将冻结30秒）";
            case 2:
                return "登录失败3次，请注意（5次错误将冻结30秒）";
            case 1:
                return "登录失败4次，再失败1次将冻结30秒";
            default:
                return "用户名或密码错误，请检查输入后重试";
        }
    }

    private static class AttemptState {
        int count = 0;
        int freezeLevel = 0;
        long freezeUntil = 0;
    }
}
