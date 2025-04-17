package com.example.ordertracker.config;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.example.ordertracker.service.JwtService;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  private final JwtService jwtService;

  public CustomJwtAuthenticationConverter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    String email = jwt.getSubject();

    UserDetails userDetails = jwtService.getUserDetails(email)
      .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found."));

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    System.out.println(authorities);
    return new UsernamePasswordAuthenticationToken(
      userDetails,
      null,
      authorities
    );
  }
}

