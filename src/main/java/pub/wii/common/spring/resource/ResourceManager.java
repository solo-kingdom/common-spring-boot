package pub.wii.common.spring.resource;

import org.apache.commons.lang3.Validate;
import pub.wii.common.file.FileUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResourceManager {
    private final Map<ResourceType, Map<String, ResourceAccessor>> sm = new HashMap<>();

    public ResourceAccessor getAccessor(ResourceType tp, String resource) {
        Map<String, ResourceAccessor> sr = sm.computeIfAbsent(tp, k -> new HashMap<>());
        return sr.computeIfAbsent(resource, k -> buildAccessor(tp, resource));
    }

    private ResourceAccessor buildAccessor(ResourceType tp, String resource) {
        switch (tp) {
            case FILE:
                return buildFileAccessor(resource);
            case ENV:
                return EnvResourceAccessor.build();
            default:
                throw new IllegalArgumentException("unsupported resource type " + tp.name());
        }
    }

    private ResourceAccessor buildFileAccessor(String resource) {
        Optional<String> ext = FileUtils.getFileExtension(resource);
        Validate.isTrue(ext.isPresent(), String.format("no file extension. [resource=%s]", resource));
        switch (ext.get()) {
            case "properties":
            case "prop":
                return PropertiesFileAccessor.build(resource);
            default:
                throw new IllegalArgumentException("unsupported resource file extension " + ext);
        }
    }
}
