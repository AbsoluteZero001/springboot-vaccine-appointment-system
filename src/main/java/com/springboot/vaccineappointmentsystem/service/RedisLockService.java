package com.springboot.vaccineappointmentsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {

    private static final Logger log = LoggerFactory.getLogger(RedisLockService.class);
    private static final long DEFAULT_TIMEOUT = 5000;
    private static final long DEFAULT_WAIT = 100;
    private final Map<String, Long> localLocks = new ConcurrentHashMap<>();

    private static final String LOCK_PREFIX = "lock:";
    private final Set<String> localLockKeys = ConcurrentHashMap.newKeySet();
    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    public boolean tryLock(String key) {
        return tryLock(key, DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        String lockKey = LOCK_PREFIX + key;

        if (redisTemplate != null) {
            try {
                long expireTime = unit.toMillis(timeout);
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < expireTime) {
                    Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", timeout, unit);
                    if (Boolean.TRUE.equals(success)) {
                        return true;
                    }
                    Thread.sleep(DEFAULT_WAIT);
                }
                return false;
            } catch (Exception e) {
                log.warn("Redis unavailable, falling back to local lock: {}", e.getMessage());
            }
        }

        // Local in-memory lock fallback
        long expireTime = unit.toMillis(timeout);
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < expireTime) {
            Long previous = localLocks.putIfAbsent(lockKey, System.currentTimeMillis());
            if (previous == null) {
                localLockKeys.add(lockKey);
                return true;
            }
            // Clean up stale locks (holder likely crashed)
            if (System.currentTimeMillis() - previous > expireTime * 2) {
                localLocks.remove(lockKey);
                Long retry = localLocks.putIfAbsent(lockKey, System.currentTimeMillis());
                if (retry == null) {
                    localLockKeys.add(lockKey);
                    return true;
                }
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

    public void unlock(String key) {
        String lockKey = LOCK_PREFIX + key;

        if (localLockKeys.remove(lockKey)) {
            localLocks.remove(lockKey);
            return;
        }

        if (redisTemplate != null) {
            try {
                redisTemplate.delete(lockKey);
            } catch (Exception e) {
                log.warn("Failed to release Redis lock: {}", e.getMessage());
                localLocks.remove(lockKey);
            }
        }
    }

    public boolean lockForAppointment(Long userId, Long vaccineId) {
        String lockKey = "appointment:user:" + userId + ":vaccine:" + vaccineId;
        return tryLock(lockKey);
    }

    public void unlockForAppointment(Long userId, Long vaccineId) {
        String lockKey = "appointment:user:" + userId + ":vaccine:" + vaccineId;
        unlock(lockKey);
    }
}
