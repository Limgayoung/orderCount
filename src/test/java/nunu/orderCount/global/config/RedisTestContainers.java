package nunu.orderCount.global.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisTestContainers implements BeforeAllCallback {
    private static final String REDIS_DOCKER_IMAGE = "redis:5.0.3-alpine";
    private GenericContainer REDIS_CONTAINER;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
                .withExposedPorts(6379)
                .withReuse(true);
        REDIS_CONTAINER.start();

        System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.redis.port", String.valueOf(REDIS_CONTAINER.getMappedPort(6379)));
    }
}
