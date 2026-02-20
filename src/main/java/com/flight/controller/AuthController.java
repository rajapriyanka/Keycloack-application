package com.flight.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.flight.dto.ForgotPasswordRequestDto;
import com.flight.service.UserService;
import com.flight.util.ApiError;
import com.flight.util.ApiResponse;
import com.flight.util.ErrorCode;
import com.flight.util.SuccessMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService userService;
	@Value("${keycloak.url}")
	private String keycloakTokenUrl;

	@Value("${keycloak.client-id}")
	private String clientId;

	@Value("${keycloak.client-secret}")
	private String clientSecret;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
		try {
			boolean exists = userService.getUserByUsernameOptional(username).isPresent();

			if (!exists) {
				ApiError apiError = new ApiError(ErrorCode.USER_NOT_FOUND,
						"User with username '" + username + "' does not exist");
				return ResponseEntity.ok(ApiResponse.failure(apiError.getResponseCode(), List.of(apiError)));
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
			body.add("grant_type", "password");
			body.add("client_id", clientId);
			body.add("client_secret", clientSecret);
			body.add("username", username);
			body.add("password", password);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(keycloakTokenUrl, request, String.class);
			return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

		} catch (HttpClientErrorException.Unauthorized ex) {
			ApiError apiError = new ApiError(ErrorCode.ACCESS_DENIED, "Invalid username or password");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(ApiResponse.failure(apiError.getResponseCode(), List.of(apiError)));
		} catch (Exception ex) {
			ApiError apiError = new ApiError(ErrorCode.INTERNAL_ERROR, ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.failure(apiError.getResponseCode(), List.of(apiError)));
		}
	}

	@GetMapping("/authenticated-user")
	public ResponseEntity<?> getLoggedInUser(@AuthenticationPrincipal Jwt jwt) {
		if (jwt == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
		}
		String username = jwt.getClaim("preferred_username");
		return ResponseEntity.ok("Authenticated user: " + username);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<?>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {

		try {
			String result = userService.handleForgotPassword(request);
			List<SuccessMessage> messages = List.of(new SuccessMessage(result));
			return ResponseEntity.ok(ApiResponse.success(messages));

		} catch (IllegalArgumentException ex) {
			ApiError apiError = new ApiError(ErrorCode.INVALID_REQUEST, ex.getMessage());
			return ResponseEntity.ok().body(ApiResponse.failure(apiError.getResponseCode(), List.of(apiError)));

		} catch (RuntimeException ex) {
			ApiError apiError = new ApiError(ErrorCode.USER_NOT_FOUND, ex.getMessage());
			return ResponseEntity.status(HttpStatus.OK)
					.body(ApiResponse.failure(apiError.getResponseCode(), List.of(apiError)));
		}
	}
}
