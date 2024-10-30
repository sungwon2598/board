package ict.board.service.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginAttemptService {
    private static final int MAX_ATTEMPT = 5;
    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        attemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }

    public int getRemainingAttempts(String key) {
        try {
            int failedAttempts = attemptsCache.get(key);
            return Math.max(0, MAX_ATTEMPT - failedAttempts);
        } catch (ExecutionException e) {
            return MAX_ATTEMPT;
        }
    }

    public int getFailedAttempts(String key) {
        try {
            return attemptsCache.get(key);
        } catch (ExecutionException e) {
            return 0;
        }
    }
}
