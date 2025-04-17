package com.example.ordertracker.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.example.ordertracker.entity.RefreshToken;
import com.example.ordertracker.repository.RefreshTokenRepository;
import com.example.ordertracker.service.JwtService;
import com.example.ordertracker.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRefreshTokenFilter extends OncePerRequestFilter {

	private final JwtDecoder jwtDecoder;
	private final JwtService jwtService;
	private final RefreshTokenRepository refreshTokenRepo;

	public JwtRefreshTokenFilter(JwtDecoder jwtDecoder, JwtService jwtService, RefreshTokenRepository refreshTokenRepo) {
		super();
		this.jwtDecoder = jwtDecoder;
		this.jwtService = jwtService;
		this.refreshTokenRepo = refreshTokenRepo;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);
		Jwt jwtToken = jwtDecoder.decode(token);

		String email = jwtToken.getSubject();
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			RefreshToken refreshToken = refreshTokenRepo.findByToken(jwtToken.getTokenValue())
							.filter(a -> !a.getRevoked())
							.orElseThrow(()->new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Refresh token revoked"));
			
			UserDetails userDetails = refreshToken.getUser();
			if (JwtUtil.isTokenValid(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);

	}
}
