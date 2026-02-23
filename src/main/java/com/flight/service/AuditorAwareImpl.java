package com.flight.service;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.flight.repository.UserRepository;
import com.flight.entity.User;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<Long> {

	private final UserRepository userRepository;

	public AuditorAwareImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Optional<Long> getCurrentAuditor() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof Jwt jwt) {

			String username = jwt.getClaim("preferred_username");

			Optional<User> user = userRepository.findByUsername(username);

			return user.map(User::getId);
		}

		return Optional.empty();
	}
}