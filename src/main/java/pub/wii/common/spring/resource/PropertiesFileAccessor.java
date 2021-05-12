package pub.wii.common.spring.resource;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertiesFileAccessor implements ResourceAccessor {
    String resource;
    Properties prop = new Properties();

    public PropertiesFileAccessor(String resource) {
        this.resource = resource;
        try {
            InputStream ips = new FileInputStream(resource);
            log.debug("load properties config file {}, with data {}", resource, this.prop);
            prop.load(ips);
        } catch (IOException e) {
            throw new IllegalArgumentException("load properties config file " + resource + " failed", e);
        }
    }

    @Override
    public String get(String name) {
        Object r = prop.get(name);
        return r == null ? null : String.valueOf(r);
    }

    public static ResourceAccessor build(String resource) {
        return new PropertiesFileAccessor(resource);
    }
}
