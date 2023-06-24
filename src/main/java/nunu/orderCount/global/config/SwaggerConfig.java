package nunu.orderCount.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "orderCount API",
                description = "orderCount API 목록입니다.",
                version = "v1.0"
        )
)
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi nonSecurityGroup(){ //jwt 토큰 불필요한 api
        return GroupedOpenApi.builder()
                .group("token 불필요 API")
                .pathsToMatch("/auth/**", "/test/**")
                .build();
    }

    @Bean
    public GroupedOpenApi securityGroup(){ //jwt 토큰 필요한 api
        return GroupedOpenApi.builder()
                .group("token 필요 API")
                .pathsToExclude("/auth/**", "/test/**")
                .build();
    }

    //todo: security 적용 시 jwt 토큰 한 번에 설정할 수 있게 해야함
}
