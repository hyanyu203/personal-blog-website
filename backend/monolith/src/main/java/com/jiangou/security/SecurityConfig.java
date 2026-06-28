package com.jiangou.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity   // replaces deprecated @EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JsonAuthenticationEntryPoint authenticationEntryPoint;
    private final JsonAccessDeniedHandler accessDeniedHandler;

    @Value("${springdoc.swagger-ui.enabled:true}")
    private boolean swaggerUiEnabled;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JsonAuthenticationEntryPoint authenticationEntryPoint,
                          JsonAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry auth =
                http.authorizeRequests();
        if (swaggerUiEnabled) {
            auth.antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll();
        }
        auth
                .antMatchers("/actuator/health", "/actuator/info").permitAll()
                .antMatchers("/actuator/prometheus")
                        .access("@prometheusAccess.canScrape(request, authentication)")
                .antMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/refresh",
                        "/api/v1/auth/oauth/exchange", "/api/v1/auth/logout",
                        "/api/v1/auth/register/send-code", "/api/v1/auth/register",
                        "/api/v1/auth/forgot-password/send-code", "/api/v1/auth/reset-password",
                        "/api/v1/subscriptions", "/api/v1/subscriptions/confirm",
                        "/api/v1/subscriptions/unsubscribe").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/auth/captcha").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/auth/github", "/api/v1/auth/github/callback",
                        "/api/v1/auth/github/enabled").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/snippets/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/snippets/*/copy").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/snippets/*/like").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/notes/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/projects/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/friend-links").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/friend-links/apply").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/rss/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/articles/archives").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/articles/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/tags/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/comments").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/comments").authenticated()
                .antMatchers(HttpMethod.POST, "/api/v1/comments/*/like").authenticated()
                .antMatchers(HttpMethod.POST, "/api/v1/notes/*/like").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/search/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/settings/public", "/api/v1/stats", "/api/v1/home").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/webmention").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/articles/*/like").authenticated()
                .antMatchers("/api/v1/admin/**").access("@adminAccess.canAccessAdminApi(authentication)")
                .anyRequest().denyAll();

        http.headers()
                .frameOptions().deny()
                .contentTypeOptions();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
