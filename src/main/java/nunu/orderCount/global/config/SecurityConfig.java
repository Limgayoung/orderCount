package nunu.orderCount.global.config;

import lombok.RequiredArgsConstructor;
import nunu.orderCount.global.config.jwt.JwtFilter;
import nunu.orderCount.global.config.security.CustomSecurityException;
import nunu.orderCount.global.error.ErrorCode;
import nunu.orderCount.global.error.ExceptionHandlerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
//@EnableGlobalMethodSecurity(securedEnabled = true) //controller api 별로 다르게 적용하고 싶을 때 사용
@Order(1)
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .httpBasic().disable()//문자열 Base64로 인코딩
                .csrf().disable() //쿠키 기반 x -> 사용 x
                .cors().and()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션 생성, 사용 x
                .and()

                .authorizeRequests()
                //swagger, test 관련 모든 권한 승인
                .antMatchers(
                        "/swagger-ui/**", "/api-docs/**", "swagger-resources/**", "/test/**"
                ).permitAll()
                //join, login, reissue 시 권한 승인
                .antMatchers("/members/join", "/members/login", "/members/reissue").permitAll()
                //admin 권한 허용
                .antMatchers("/admin/**").hasRole("ADMIN")
                //그 외 모든 member 권한 허용
                .antMatchers("/**").hasAnyRole("OWNER", "ADMIN")
                .anyRequest().denyAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtFilter.class)

                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() { //권한 문제
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        throw new CustomSecurityException(ErrorCode.PERMISSION_DENIED);
                    }
                }) //todo: 권한 문제 발생 시 에러처리 - message 추가할 것
                .authenticationEntryPoint(new AuthenticationEntryPoint() { //인증 문제
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        throw new CustomSecurityException(ErrorCode.INVALID_TOKEN);
                    }
                }); //todo: 인증 문제 발생 시 에러처리 - message 추가할 것
                
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
