package com.example.studentmanagementsystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CSRF protection (don't disable it)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login/**", "/register/**", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("TEACHER")
                        .requestMatchers("/teachers/**").hasRole("TEACHER")
                        .requestMatchers("/courses/**").hasRole("TEACHER")
                        .requestMatchers("/departments/**").hasRole("TEACHER")
                        .requestMatchers("/students/*/courses/**").authenticated() // Students manage their own courses
                        .requestMatchers("/students/**").authenticated()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}
