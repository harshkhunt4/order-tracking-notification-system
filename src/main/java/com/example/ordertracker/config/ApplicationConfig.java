package com.example.ordertracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.example.ordertracker.model.RsaKeyRecord;
import com.example.ordertracker.repository.RefreshTokenRepository;
import com.example.ordertracker.repository.UserRepository;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class ApplicationConfig {

  private final RsaKeyRecord rsaKeyRecord;
  private final UserRepository userRepo;
  private final RefreshTokenRepository refreshTokenRepo;

  public ApplicationConfig(RsaKeyRecord rsaKeyRecord, UserRepository userRepo, RefreshTokenRepository refreshTokenRepo) {
    super();
    this.rsaKeyRecord = rsaKeyRecord;
    this.userRepo = userRepo;
    this.refreshTokenRepo = refreshTokenRepo;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
  }

  @Bean
  JwtEncoder jwtEncoder() {
    JWK jwk = new RSAKey.Builder(rsaKeyRecord.rsaPublicKey())
        .privateKey(rsaKeyRecord.rsaPrivateKey()).build();
    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSource);
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  UserDetailsService userDetailsService() {
    return username -> userRepo.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
  
  @Bean
  LogoutHandler logoutHandler() {
    return (request, response, authentication) -> {
      String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      if(!authHeader.startsWith(TokenType.BEARER.getValue())) {
        return;
      }
      String refreshToken = authHeader.substring(7);
      
      refreshTokenRepo.findByToken(refreshToken)
          .map(token -> {
              token.setRevoked(true);
              refreshTokenRepo.save(token);
              return token;
          });
    };
  }
}
