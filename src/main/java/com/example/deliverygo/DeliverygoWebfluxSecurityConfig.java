package com.example.deliverygo;

import static org.springframework.security.config.Customizer.withDefaults;

import com.example.deliverygo.userdetails.Oauth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;

@EnableWebFluxSecurity
public class DeliverygoWebfluxSecurityConfig {

	@Autowired
	private Oauth2AuthenticationSuccessHandler oauth2authSuccessHandler;

	@Bean
	public MapReactiveUserDetailsService userDetailsService() {
		UserDetails user = User
				.withDefaultPasswordEncoder()
				.username("user")
				.password("user")
				.roles("USER")
				.build();
		return new MapReactiveUserDetailsService(user);
	}

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
				//.csrf()
				//.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
				//.and()

				.authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
				.httpBasic(withDefaults())
				.formLogin(withDefaults())
		    .oauth2Login()
				.authenticationSuccessHandler(oauth2authSuccessHandler);

		http
				.csrf()
				.disable();

		return http.build();
	}
}
