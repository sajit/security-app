package edu.umd.cysec.capstone.securityapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import edu.umd.cysec.capstone.securityapp.service.MongoUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    RequestMatcher requestMatcher;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .mvcMatchers("/","/register-success","/login","/register")
                        .permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
       return new MongoUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
