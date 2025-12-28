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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                
                // 2. Cho phép các trang Public (Login, Register, Trang chủ)
                .requestMatchers("/", "/login", "/register", "/error").permitAll()
                
                // 3. Cho phép GraphQL (Để làm search)
                .requestMatchers("/graphql/**", "/graphiql/**").permitAll()

                // 4. Phân quyền cho các Role cụ thể
                .requestMatchers("/student/**").hasAuthority("STUDENT")
                .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "ADVISOR")

                // Các request còn lại phải đăng nhập
                .anyRequest().authenticated()
            )
            .formLogin(
                form -> form
                    .loginPage("/login") // Đường dẫn tới Controller login
                    .loginProcessingUrl("/login") // Action trong form HTML
                    .defaultSuccessUrl("/default", true) // Xử lý chuyển hướng sau khi login thành công (xem Controller bên dưới)
                    .permitAll()
            )
            .logout(
                logout -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            )
            // Tắt CSRF cho GraphQL endpoint để gọi từ JS dễ dàng hơn
            .csrf(csrf -> csrf.ignoringRequestMatchers("/graphql/**"));

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
}