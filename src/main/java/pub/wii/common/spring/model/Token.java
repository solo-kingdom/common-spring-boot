package pub.wii.common.spring.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Token {
    private long uid;
    private String token;
}
