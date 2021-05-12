package pub.wii.common.spring.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import pub.wii.common.spring.resource.ResourceAccessor;
import pub.wii.common.spring.resource.ResourceManager;
import pub.wii.common.spring.resource.ResourceType;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EncryptedEnvironmentProcessor implements EnvironmentPostProcessor {
    private static final String REGEX = "^encrypt\\{(.*)}$";
    private static final Pattern PT = Pattern.compile(REGEX);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        HashMap<String, Object> props = new HashMap<>();
        ResourceManager rm = new ResourceManager();

        for (PropertySource<?> ps : environment.getPropertySources()) {
            if (ps instanceof OriginTrackedMapPropertySource) {
                OriginTrackedMapPropertySource source = (OriginTrackedMapPropertySource) ps;
                for (String name : source.getPropertyNames()) {
                    Object value = source.getProperty(name);
                    if (value instanceof String) {
                        String v = (String) value;
                        String data = getMatch(v);
                        if (StringUtils.isNotBlank(data)) {
                            data = environment.resolvePlaceholders(data);
                            String[] ds = data.split(":", 3);
                            log.info("matching config found {}: {}, split {}", name, data, ds);
                            if (ds.length >= 2) {
                                String resource = ds.length == 2 ? null : ds[1];
                                ResourceAccessor ra = rm.getAccessor(ResourceType.nameOf(ds[0]), resource);
                                String dv = ra.get(ds[ds.length - 1]);
                                props.put(name, dv);
                                log.debug("config item found {}-{}, {}: {}", ds[0], resource, ds[ds.length - 1], dv);
                            } else {
                                throw new IllegalArgumentException("illegal config entry, " + name + " - " + data);
                            }
                        }
                    }
                }
            }
        }
        if (!props.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("secretDecryptedConfig", props));
        }
    }

    static String getMatch(String value) {
        Matcher m = PT.matcher(value);
        if (m.matches()) {
            return m.group(1);
        } else {
            return null;
        }
    }
}
