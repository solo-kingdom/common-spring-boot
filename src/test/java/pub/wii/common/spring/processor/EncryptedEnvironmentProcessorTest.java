package pub.wii.common.spring.processor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncryptedEnvironmentProcessorTest {
    @Test
    public void testMatch() {
        String res = EncryptedEnvironmentProcessor.getMatch("encrypt{file:mysql.username}");
        assertEquals(res, "file:mysql.username", "extract encrypted data failed");
    }
}