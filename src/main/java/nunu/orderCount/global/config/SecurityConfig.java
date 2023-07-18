package nunu.orderCount.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> {
            web.ignoring()
                    .antMatchers(
                            "/swagger-ui/**", "/api-document/**", "/test/**"
                    );
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .httpBasic().disable()//문자열 Base64로 인코딩
                .csrf().disable() //쿠키 기반 x -> 사용 x
                .cors().and()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션 생성/사용 x
                .and()

                .authorizeRequests()
                //swagger, test 관련 모든 권한 승인
                .antMatchers( 
                        "/swagger-ui/**", "/api-document/**", "/test/**"
                ).permitAll()
                //join, login 시 권한 승인
                .antMatchers("/members/join", "/members/login").permitAll() 
                //admin 권한 허용
                .antMatchers("/admin/**").hasRole("ROLE_ADMIN")
                //그 외 모든 member 권한 허용
                .antMatchers("/**").hasAnyRole("ROLE_OWNER", "ROLE_ADMIN")
                .anyRequest().denyAll()
                .and()

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) //todo: JwtFilter class 생성
                .exceptionHandling()
                .accessDeniedHandler() //todo: 권한 문제 발생 시 에러처리
                .authenticationEntryPoint(); //todo: 인증 문제 발생 시 에러처리
                
        return http.build();
    }
}
