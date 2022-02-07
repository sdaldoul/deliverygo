package com.example.deliverygo.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import com.example.deliverygo.userdetails.Oauth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@EnableWebFluxSecurity
public class DeliverygoWebfluxSecurityConfig {

	@Autowired
	private Oauth2AuthenticationSuccessHandler oauth2authSuccessHandler;

	/*@Bean
	public MapReactiveUserDetailsService userDetailsService() {
		UserDetails user = User
				.withDefaultPasswordEncoder()
				.username("user")
				.password("user")
				.roles("USER")
				.build();
		UserDetails user2 = User
				.withDefaultPasswordEncoder()
				.username("admin")
				.password("admin")
				.roles("ADMIN")
				.build();
		return new MapReactiveUserDetailsService(user,user2);
	}*/

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
				//.headers().cache().disable().and() 		-->		//.cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
			/*	.csrf()
				.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
				.and()*/
				.securityMatcher(new NegatedServerWebExchangeMatcher(
						ServerWebExchangeMatchers.pathMatchers("/css/**", "/webjars/**")))
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers("/register", "/login","/logout", "/login-error", "/login-verified").permitAll()
						.pathMatchers("/support/admin/**").hasRole("ADMIN")
						.anyExchange().authenticated())
				.formLogin().loginPage("/login")
				.and()
				.httpBasic(withDefaults())
				.formLogin(withDefaults())
		    .oauth2Login()
				.authenticationSuccessHandler(oauth2authSuccessHandler);

		http
				.csrf()
				.disable();

		return http.build();
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		DelegatingPasswordEncoder encoder =  (DelegatingPasswordEncoder) PasswordEncoderFactories
				.createDelegatingPasswordEncoder();
		return encoder;
	}

}
