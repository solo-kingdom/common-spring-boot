package pub.wii.common.spring.resource;

public class EnvResourceAccessor implements ResourceAccessor {
    @Override
    public String get(String name) {
        return System.getProperty(name);
    }

    public static ResourceAccessor build() {
        return new EnvResourceAccessor();
    }
}
