package com.springboot.vaccineappointmentsystem.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long FREEZE_30S_MS = 30_000;
    private static final long FREEZE_60S_MS = 60_000;

    private final ConcurrentHashMap<String, AttemptState> attempts = new ConcurrentHashMap<>();

    /**
     * Check if the username is currently frozen.
     *
     * @return null if login is allowed, or an error message if blocked.
     */
    public String checkBlocked(String username) {
        AttemptState state = attempts.get(username);
        if (state == null) {
            return null;
        }
        if (state.freezeUntil > 0) {
            long remaining = state.freezeUntil - System.currentTimeMillis();
            if (remaining > 0) {
                long seconds = (remaining + 999) / 1000;
                if (state.freezeLevel == 1) {
                    return "登录失败次数过多，已临时冻结" + seconds + "秒，请稍后再试";
                } else {
                    return "登录失败次数过多，已进入限制状态（每" + MAX_ATTEMPTS + "次错误将冻结1分钟）";
                }
            }
            // Freeze expired — reset count but keep freezeLevel for upgrade tracking
            state.count = 0;
            state.freezeUntil = 0;
        }
        return null;
    }

    /**
     * Record a failed login attempt. Must only be called after checkBlocked returns null.
     *
     * @return error message for the current failure (generic or freeze notice).
     */
    public String recordFailedAttempt(String username) {
        AttemptState state = attempts.computeIfAbsent(username, k -> new AttemptState());
        state.count++;

        if (state.count >= MAX_ATTEMPTS) {
            state.count = 0;
            if (state.freezeLevel == 0) {
                state.freezeLevel = 1;
                state.freezeUntil = System.currentTimeMillis() + FREEZE_30S_MS;
                return "登录失败次数过多，已临时冻结30秒，请稍后再试";
            } else {
                state.freezeLevel = 2;
                state.freezeUntil = System.currentTimeMillis() + FREEZE_60S_MS;
                return "登录失败次数过多，已进入限制状态（每" + MAX_ATTEMPTS + "次错误将冻结1分钟）";
            }
        }

        return "用户名或密码错误，请检查输入后重试";
    }

    /**
     * Record a successful login — resets all state for this username.
     */
    public void recordSuccess(String username) {
        attempts.remove(username);
    }

    private static class AttemptState {
        int count = 0;
        int freezeLevel = 0; // 0=none, 1=first freeze(30s), 2=second freeze(60s)
        long freezeUntil = 0; // epoch millis
    }
}
