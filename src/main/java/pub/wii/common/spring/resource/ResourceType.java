package pub.wii.common.spring.resource;

public enum ResourceType {
    FILE("file"),
    ENV("env");

    String tp;

    ResourceType(String tp) {
        this.tp = tp;
    }

    public static ResourceType nameOf(String name) {
        return valueOf(name.toUpperCase());
    }
}
