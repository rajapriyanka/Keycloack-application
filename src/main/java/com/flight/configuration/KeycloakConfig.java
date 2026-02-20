package com.flight.configuration;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

	@Bean
	public Keycloak keycloak() {
		return KeycloakBuilder.builder().serverUrl("http://localhost:8085").realm("FlightReservationSystem")
				.clientId("flight-client").clientSecret("lmNzJZDDd4g6GJ7LWvJdIWjRKKquPkFN").username("admin")
				.password("SecurePass123").build();
	}
}
