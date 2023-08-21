package nunu.orderCount.global.config;

import io.netty.handler.codec.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods(
                        HttpMethod.POST.name(), HttpMethod.GET.name(), HttpMethod.DELETE.name(), HttpMethod.PUT.name()
                )
                .allowedHeaders("*")
                .allowedOrigins("*");
    }
}
