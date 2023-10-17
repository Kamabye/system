package com.domain.system.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SecurityConfig {
	
	/**
	 * No se agrega system a la url porque lo agrega el contextpath del application.properties
	 */
	public static final String[] ENDPOINTS_WHITELIST = {
			"apiv1/**"
			};

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						auth -> auth.requestMatchers(ENDPOINTS_WHITELIST).permitAll().anyRequest().authenticated())
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				//.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
				.httpBasic();
		return http.build();
	}

}