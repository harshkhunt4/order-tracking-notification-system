package com.example.ordertracker.util;

import java.time.Instant;
import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtUtil {

	private JwtUtil() {
		
	}
	
	public static String getUserName(Jwt jwtToken) {
		return jwtToken.getSubject();
	}
	
	public static boolean isTokenValid(Jwt jwtToken,UserDetails userDetails) {
		String email = getUserName(jwtToken);
		boolean isTokenExpired = getIfIsTokenExpired(jwtToken);
		boolean isTokenUserSameAsDB = email.equals(userDetails.getUsername());
		return !isTokenExpired && isTokenUserSameAsDB;
	}

	private static boolean getIfIsTokenExpired(Jwt jwtToken) {
		return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
	}
}
