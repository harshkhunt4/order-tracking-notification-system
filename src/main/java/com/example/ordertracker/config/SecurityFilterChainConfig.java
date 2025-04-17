package com.example.ordertracker.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.ordertracker.filter.JwtRefreshTokenFilter;
import com.example.ordertracker.repository.RefreshTokenRepository;
import com.example.ordertracker.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityFilterChainConfig {

  private final JwtDecoder jwtDecoder;
  private final JwtService jwtService;
  private final UserDetailsService userDetailService;
  private final RefreshTokenRepository refreshTokenRepo;
  private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
  private final LogoutHandler logoutHandler;
  
  public SecurityFilterChainConfig(JwtDecoder jwtDecoder,
      JwtService jwtService, UserDetailsService userDetailService, CustomJwtAuthenticationConverter customJwtAuthenticationConverter, RefreshTokenRepository refreshTokenRepo, LogoutHandler logoutHandler) {
    super();
    this.jwtDecoder = jwtDecoder;
    this.jwtService = jwtService;
    this.userDetailService = userDetailService;
    this.refreshTokenRepo = refreshTokenRepo;
    this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
    this.logoutHandler = logoutHandler;
  }

  @Order(1)
  @Bean
  SecurityFilterChain signInFilterChain(HttpSecurity http) throws Exception {
      applyCommonStatelessConfig(http);
      http
          .securityMatcher("api/auth/sign-in/**")
          .authorizeHttpRequests(req -> req.anyRequest().authenticated())
          .userDetailsService(userDetailService)
          .exceptionHandling(ex -> ex.authenticationEntryPoint(
              (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()))
          )
          .httpBasic(withDefaults());

      return http.build();
  }

  @Order(2)
  @Bean
  SecurityFilterChain usersApiFilterChain(HttpSecurity http) throws Exception {
      applyCommonStatelessConfig(http);
      http
          .securityMatcher(new AntPathRequestMatcher("/api/users/**"))
          .authorizeHttpRequests(req -> req.anyRequest().authenticated())
          .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
              .jwtAuthenticationConverter(customJwtAuthenticationConverter)));
          //.addFilterBefore(new JwtAuthFilter(jwtDecoder, jwtService), UsernamePasswordAuthenticationFilter.class);
      applyCommonExceptionHandling(http);
      http.httpBasic(withDefaults());
      return http.build();
  }

  @Order(3)
  @Bean
  SecurityFilterChain refreshTokenFilterChain(HttpSecurity http) throws Exception {
      applyCommonStatelessConfig(http);
      http
          .securityMatcher("api/auth/refresh-token/**")
          .authorizeHttpRequests(req -> req.anyRequest().authenticated())
          //.oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
          .addFilterBefore(new JwtRefreshTokenFilter(jwtDecoder, jwtService, refreshTokenRepo), UsernamePasswordAuthenticationFilter.class);
      applyCommonExceptionHandling(http);
      http.httpBasic(withDefaults());
      return http.build();
  }

  @Order(4)
  @Bean
  SecurityFilterChain logoutFilterChain(HttpSecurity http) throws Exception {
      applyCommonStatelessConfig(http);
      http
          .securityMatcher("/api/logout/**")
          .authorizeHttpRequests(req -> req.anyRequest().authenticated())
          .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
              .jwtAuthenticationConverter(customJwtAuthenticationConverter)))
          //.addFilterBefore(new JwtAuthFilter(jwtDecoder, jwtService), UsernamePasswordAuthenticationFilter.class)
          .logout(logout -> logout
              .logoutUrl("/api/logout")
              .addLogoutHandler(logoutHandler)
              .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
          );
      applyCommonExceptionHandling(http);
      return http.build();
  }

  @Order(5)
  @Bean
  SecurityFilterChain signUpFilterChain(HttpSecurity http) throws Exception {
      applyCommonStatelessConfig(http);
      http
          .securityMatcher("/api/auth/sign-up/**")
          .authorizeHttpRequests(req -> req.anyRequest().permitAll());
      return http.build();
  }

  
  private HttpSecurity applyCommonStatelessConfig(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable).sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
  }

  private void applyCommonExceptionHandling(HttpSecurity http) throws Exception {
    http.exceptionHandling(ex -> {
      ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
    });
  }

}
