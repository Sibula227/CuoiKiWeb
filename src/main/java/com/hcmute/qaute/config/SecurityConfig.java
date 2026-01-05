package com.hcmute.qaute.config;

import com.hcmute.qaute.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        // 1. Cho phép truy cập tài nguyên tĩnh (CSS, JS, Ảnh)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/svg/**", "/webjars/**").permitAll()

                        // 2. Cho phép các trang Public (Login, Register, Trang chủ)
                        .requestMatchers("/", "/login", "/register", "/error").permitAll()

                        // 3. Cho phép GraphQL và Chatbot
                        .requestMatchers("/graphql/**", "/graphiql/**", "/api/chat/**").permitAll()

                        // 4. Phân quyền cho các Role cụ thể
                        .requestMatchers("/student/**").hasAuthority("STUDENT")
                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "ADVISOR")
                        // Cho phép truy cập trang quên mật khẩu và reset mật khẩu
                        .requestMatchers("/forgot-password", "/reset-password").permitAll()

                        // Các request còn lại phải đăng nhập
                        .anyRequest().authenticated())
                .formLogin(
                        form -> form
                                .loginPage("/login") // Đường dẫn tới Controller login
                                .loginProcessingUrl("/login") // Action trong form HTML
                                .defaultSuccessUrl("/default", true) // Xử lý chuyển hướng sau khi login thành công (xem
                                                                     // Controller bên dưới)
                                .permitAll())
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/login?logout")
                                .permitAll())
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler()))
                // Tắt CSRF cho GraphQL và Chat API
                .csrf(csrf -> csrf.ignoringRequestMatchers("/graphql/**", "/api/chat/**"));

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response,
                    AccessDeniedException accessDeniedException) throws java.io.IOException {
                // Chuyển hướng về trang login với thông báo lỗi
                response.sendRedirect("/login?accessDenied=true");
            }
        };
    }
}