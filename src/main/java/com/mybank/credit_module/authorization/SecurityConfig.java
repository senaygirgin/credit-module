package com.mybank.credit_module.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
    @Profile("dev")
    @Bean
    public SecurityFilterChain securityFilterChainDev(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/**")) // Disable CSRF for simplicity (enable it in production)
            .authorizeHttpRequests(request -> {request
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated();
            })
            .httpBasic(Customizer.withDefaults()); // Use HTTP Basic authentication
        return http.build();
    }

    @Profile("prod")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> {request
                        .anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults()); // Use HTTP Basic authentication
        return http.build();
    }

    @Profile("dev")
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }

/*
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for encoding passwords
    }

 */
}
