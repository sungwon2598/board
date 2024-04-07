package ict.board.util.cache;


import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeCache {

    private static final long EXPIRATION_TIME = 600000; // 10분을 밀리초로 환산
    private final ConcurrentHashMap<String, SimpleEntry<String, Long>> cache = new ConcurrentHashMap<>();

    public void storeCode(String email, String code) {
        cleanExpiredCodes(); // 캐시 정리 호출
        long expirationTime = System.currentTimeMillis() + EXPIRATION_TIME;
        cache.put(email, new SimpleEntry<>(code, expirationTime));
    }

    public boolean isValidCode(String email, String code) {
        cleanExpiredCodes(); // 캐시 정리 호출
        SimpleEntry<String, Long> entry = cache.getOrDefault(email, null);

        if (entry == null || !entry.getKey().equals(code)) {
            return false;
        }

        if (System.currentTimeMillis() > entry.getValue()) {
            cache.remove(email);
            return false;
        }

        return true;
    }

    public void cleanExpiredCodes() {
        long currentTime = System.currentTimeMillis();
        Iterator<Entry<String, SimpleEntry<String, Long>>> iterator = cache.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, SimpleEntry<String, Long>> entry = iterator.next();
            if (currentTime > entry.getValue().getValue()) {
                iterator.remove();
            }
        }
    }
}
