package com.springboot.vaccineappointmentsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_TIMEOUT = 5000; // 5 seconds
    private static final long DEFAULT_WAIT = 100; // 100 ms

    /**
     * Try to acquire a lock with default timeout
     * @param key lock key
     * @return true if lock acquired, false otherwise
     */
    public boolean tryLock(String key) {
        return tryLock(key, DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    /**
     * Try to acquire a lock with custom timeout
     * @param key lock key
     * @param timeout timeout duration
     * @param unit time unit
     * @return true if lock acquired, false otherwise
     */
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        String lockKey = LOCK_PREFIX + key;
        long expireTime = unit.toMillis(timeout);
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < expireTime) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", timeout, unit);
            if (Boolean.TRUE.equals(success)) {
                return true;
            }
            try {
                Thread.sleep(DEFAULT_WAIT);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    /**
     * Release a lock
     * @param key lock key
     */
    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        redisTemplate.delete(lockKey);
    }

    /**
     * Acquire lock for user appointment to prevent duplicate appointments
     * @param userId user ID
     * @param vaccineId vaccine ID
     * @return true if lock acquired
     */
    public boolean lockForAppointment(Long userId, Long vaccineId) {
        String lockKey = "appointment:user:" + userId + ":vaccine:" + vaccineId;
        return tryLock(lockKey);
    }

    /**
     * Release lock for user appointment
     * @param userId user ID
     * @param vaccineId vaccine ID
     */
    public void unlockForAppointment(Long userId, Long vaccineId) {
        String lockKey = "appointment:user:" + userId + ":vaccine:" + vaccineId;
        unlock(lockKey);
    }
}