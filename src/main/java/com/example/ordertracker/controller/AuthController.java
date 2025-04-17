package com.example.ordertracker.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ordertracker.model.RegisterRequest;
import com.example.ordertracker.model.response.AuthenticationResponse;
import com.example.ordertracker.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  
  public AuthController(AuthService authService) {
    super();
    this.authService = authService;
  }
  @PostMapping("/sign-up")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request, HttpServletResponse response) throws Exception{
    AuthenticationResponse authResponse = authService.register(request,response);
    return ResponseEntity.ok(authResponse);
  }
  @PostMapping("/sign-in")
  public ResponseEntity<AuthenticationResponse> authenticate(
      Authentication request, HttpServletResponse response){
    return ResponseEntity.ok(authService.authenticate(request,response));
  }
  @PostMapping("/refresh-token")
  public ResponseEntity<?> getAccessToken(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
    return ResponseEntity.ok(authService.getAccessTokenUsingRefreshToken(authorizationHeader));
  }

}
