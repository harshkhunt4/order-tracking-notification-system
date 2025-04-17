package com.example.ordertracker.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.ordertracker.entity.RefreshToken;
import com.example.ordertracker.entity.User;
import com.example.ordertracker.model.RegisterRequest;
import com.example.ordertracker.model.response.AuthenticationResponse;
import com.example.ordertracker.repository.RefreshTokenRepository;
import com.example.ordertracker.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepo;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepo;

  public AuthService(JwtService jwtService, RefreshTokenRepository refreshTokenRepo,
      PasswordEncoder passwordEncoder, UserRepository userRepo) {
    super();
    this.jwtService = jwtService;
    this.refreshTokenRepo = refreshTokenRepo;
    this.passwordEncoder = passwordEncoder;
    this.userRepo = userRepo;
  }

  public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response)
      throws Exception {
    if (jwtService.getUserDetails(request.getEmail()).isPresent()) {
      throw new Exception("User Already Exist");
    }
    User user = User.builder().email(request.getEmail()).fullname(request.getFullname()).roles("ROLE_USER")
        .password(passwordEncoder.encode(request.getPassword())).build();

    userRepo.save(user);
    Authentication authentication = createAuthenticationObejct(user);

    return generateTokenAndAuthResponse(response, user, authentication);
  }

  public AuthenticationResponse authenticate(Authentication request, HttpServletResponse response) {
    User user = jwtService.getUserDetails(request.getName())
        .orElseThrow(() -> new UsernameNotFoundException("User not found.."));

    return generateTokenAndAuthResponse(response, user, request);
  }

  @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
  public Object getAccessTokenUsingRefreshToken(String authorizationHeader) {
    if (!authorizationHeader.startsWith(TokenType.BEARER.getValue())) {
      return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Please verify your token type");
    }

    String refreshToken = authorizationHeader.substring(7);

    var refreshTokenEntity = refreshTokenRepo.findByToken(refreshToken)
        .filter(token -> !token.getRevoked())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Refresh token revoked"));

    User user = refreshTokenEntity.getUser();

    Authentication authentication = createAuthenticationObejct(user);

    String accessToken = jwtService.generateToken(authentication);

    return AuthenticationResponse.builder().accessToken(accessToken).accessTokenExpiry(10)
        .userName(authentication.getName()).tokenType(TokenType.BEARER).build();
  }

  private AuthenticationResponse generateTokenAndAuthResponse(HttpServletResponse response,
      User user, Authentication authentication) {
    String token = jwtService.generateToken(authentication);
    String refreshToken = jwtService.generateRefreshToken(authentication);

    saveRefreshToken(refreshToken, user);
    createRefreshTokenCookie(response, refreshToken);

    return AuthenticationResponse.builder().accessToken(token).accessTokenExpiry(10)
        .userName(authentication.getName()).tokenType(TokenType.BEARER).build();
  }

  private void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
    Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setMaxAge(24 * 60 * 60);
    response.addCookie(refreshTokenCookie);
  }

  private void saveRefreshToken(String token, User user) {
    RefreshToken refreshToken = RefreshToken.builder().revoked(false).token(token).user(user)
        .build();
    refreshTokenRepo.save(refreshToken);
  }

  private Authentication createAuthenticationObejct(UserDetails user) {
    return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(),
        user.getAuthorities());
  }
}
