package com.example.ordertracker.model.response;

import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {

	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("access_token_expiry")
	private int accessTokenExpiry;
	
	@JsonProperty("token_type")
	private TokenType tokenType;
	
	@JsonProperty("user_name")
	private String userName;
}
