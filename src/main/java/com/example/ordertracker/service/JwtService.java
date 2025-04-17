package com.example.ordertracker.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.ordertracker.entity.User;
import com.example.ordertracker.repository.UserRepository;

@Service
public class JwtService {

	private final JwtEncoder jwtEncoder;
	private final UserRepository userRepo;

	public JwtService(JwtEncoder jwtEncoder, UserRepository userRepo) {
		super();
		this.jwtEncoder = jwtEncoder;
		this.userRepo = userRepo;
	}

	public String generateToken(Authentication authentication) {
		String roles = getRolesOfUser(authentication);
		//String permission = getPermissionFromRoles(roles);

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("aws.com")
				.subject(authentication.getName())
				.issuedAt(Instant.now())
				.expiresAt(Instant.now()
				.plus(10,ChronoUnit.MINUTES))
				.claim("scope", roles).build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	public String generateRefreshToken(Authentication authentication) {

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("aws.com")
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
				.subject(authentication.getName())
				.claim("scope", "SCOPE_REFRESH_TOKEN")
				.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	public static String getPermissionFromRoles(String roles) {
		Set<String> permissions = new HashSet<>();
		if (roles.contains("ROLE_ADMIN"))
			permissions.addAll(List.of("READ", "WRITE", "DELETE"));
		else if (roles.contains("ROLE_MANAGER"))
			permissions.add("READ");
		else if (roles.contains("ROLE_USER"))
			permissions.add("READ");
		return String.join(" ", permissions);

	}

	private String getRolesOfUser(Authentication authentication) {
		return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
	}

	public Optional<User> getUserDetails(String email) {
		return userRepo.findByEmail(email);
	}
}
