package pub.wii.common.spring.token;

import pub.wii.common.spring.model.Token;

public interface TokenManager {
    Token create(long uid);

    Token check(String token);

    void delete(String token);
}
