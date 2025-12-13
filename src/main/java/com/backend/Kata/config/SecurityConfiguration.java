package com.backend.Kata.config;

import com.backend.Kata.entities.Role;
import com.backend.Kata.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(request ->
                request.requestMatchers("/api/v1/auth/**", "/api/auth/**")
                        .permitAll()
                        // Admin-only endpoints - must come before general /api/sweets/** matcher
                        .requestMatchers(HttpMethod.DELETE, "/api/sweets/**")
                        .hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/api/sweets/**/restock")
                        .hasAuthority(Role.ADMIN.name())
                        // All other sweets endpoints require authentication
                        .requestMatchers("/api/sweets/**")
                        .authenticated()
                        .requestMatchers("/api/v1/admin").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/api/v1/user").hasAuthority(Role.USER.name())
                        .anyRequest().authenticated()).sessionManagement(
                                manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            private final UserDetailsService userDetailsService = userService.userDetailsService();
            private final PasswordEncoder encoder = passwordEncoder();

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (encoder.matches(password, userDetails.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(
                            userDetails,
                            password,
                            userDetails.getAuthorities()
                    );
                } else {
                    throw new BadCredentialsException("Invalid password");
                }
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

}
