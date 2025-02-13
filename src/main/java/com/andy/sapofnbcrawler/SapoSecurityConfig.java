package com.andy.sapofnbcrawler;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SapoSecurityConfig {
	// @Bean
	// public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
	// Exception {
	// http
	// .authorizeHttpRequests(auth -> auth
	// .requestMatchers("/", "/home").permitAll() // Allow access to the homepage
	// .anyRequest().authenticated() // Require authentication for other endpoints
	// )
	// .formLogin() // Enable form-based login (optional)
	// .and()
	// .logout(); // Enable logout functionality (optional)
	//
	// return http.build();
	// }

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		// corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
		// corsConfiguration.setAllowedOrigins(Arrays.asList("http://10.177.81.89:5173"));
		corsConfiguration.setAllowedOrigins(
				Arrays.asList("http://localhost:5173", "http://192.168.1.4:5173", "http://10.177.81.89:5173"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Authorization", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

	// @Bean
	// public WebMvcConfigurer corsConfigurer() {
	// return new WebMvcConfigurer() {
	// @Override
	// public void addCorsMappings(CorsRegistry registry) {
	// registry.addMapping("/**") // Apply CORS to all endpoints
	// .allowedOrigins("http://localhost:5173") // Allow the React frontend origin
	// .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP
	// methods
	// .allowedHeaders("*") // Allow all headers
	// .allowCredentials(true); // Allow credentials like cookies
	// }
	// };
	// }

}
