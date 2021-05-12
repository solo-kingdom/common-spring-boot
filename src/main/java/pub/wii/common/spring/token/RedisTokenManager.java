package pub.wii.common.spring.token;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pub.wii.common.spring.model.Token;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(value = "auth.type", havingValue = "token")
public class RedisTokenManager implements TokenManager {

    private static final String KEY_PREFIX = "token:";

    @Value("${token.ttl:1800}")
    private int ttl;

    @Value("${spring.application.name:global}")
    private String applicationName;

    private String prefix;

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTokenManager(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Token create(long uid) {
        String token = UUID.randomUUID().toString().replace("-", "");
        Token tk = new Token().setToken(token).setUid(uid);
        redisTemplate.opsForValue().set(getKey(token), String.valueOf(uid), ttl, TimeUnit.SECONDS);
        return tk;
    }

    @Override
    public Token check(String token) {
        String key = getKey(token);
        String uid = redisTemplate.opsForValue().get(key);
        if (uid == null) {
            return null;
        }

        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        return new Token().setToken(token).setUid(Long.parseLong(uid));
    }

    @Override
    public void delete(String token) {
        redisTemplate.delete(getKey(token));
    }

    private String getKey(String token) {
        if (StringUtils.isEmpty(prefix)) {
            this.prefix = KEY_PREFIX + applicationName + ":";
        }
        return prefix + token;
    }
}
