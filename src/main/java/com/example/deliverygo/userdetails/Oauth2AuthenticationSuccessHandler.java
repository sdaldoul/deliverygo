package com.example.deliverygo.userdetails;


import com.example.deliverygo.model.UserOAuth2Dto;
import com.example.deliverygo.repository.CryptoOauth2UserRepository;
import com.example.deliverygo.service.UserRegistrationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

@Configuration("oauth2authSuccessHandler")
@RequiredArgsConstructor
@Log4j2
public class Oauth2AuthenticationSuccessHandler extends RedirectServerAuthenticationSuccessHandler {

	private final CryptoOauth2UserRepository cryptoOauth2UserRepository;
	private final UserRegistrationService registrationService;

	@SneakyThrows
	@Override
	public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {

		return cryptoOauth2UserRepository
				.existsByUsername(authentication.getName())
				.flatMap(userFound -> {
					if (userFound) {
						log.info("FOUND TRUE");
					} else {
						log.info("NOT FOUND");
						OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
						Map<String, Object> attributes = token.getPrincipal().getAttributes();
						String firstname = null, lastname = null, email = null;
						if (token.getAuthorizedClientRegistrationId().equals("facebook")) {
							String name = attributes.get("name").toString();
							firstname = name.split(" ")[0];
							lastname = name.split(" ")[1];
							email = attributes.get("email").toString();
						} else if (token.getPrincipal() instanceof DefaultOidcUser) {
							DefaultOidcUser oidcToken = (DefaultOidcUser) token.getPrincipal();
							firstname = oidcToken.getGivenName();
							lastname = oidcToken.getFamilyName();
							email = oidcToken.getEmail();
						}
						log.info(" Oauth2AuthenticationSuccessHandler : firstname {}, lastname {} , email {}, authentication.getName() {}", firstname,
								lastname, email, authentication.getName());
						UserOAuth2Dto user = new UserOAuth2Dto(firstname, lastname, authentication.getName(), email);
						// you need to return otherwise it will not run the save to database
						return registrationService.registerNewAuth2User(user);
					}
					return Mono.just(userFound);
				})
				.then(super.onAuthenticationSuccess(webFilterExchange, authentication));

	}

/*

// Signature of the HttpClient.get method
Mono<JsonObject> get(String url);

// The two urls to call
String firstUserUrl = "my-api/first-user";
String userDetailsUrl = "my-api/users/details/"; // needs the id at the end

// Example with map
Mono<Mono<JsonObject>> result = HttpClient.get(firstUserUrl).
  map(user -> HttpClient.get(userDetailsUrl + user.getId()));
// This results with a Mono<Mono<...>> because HttpClient.get(...)
// returns a Mono

// Same example with flatMap
Mono<JsonObject> bestResult = HttpClient.get(firstUserUrl).
  flatMap(user -> HttpClient.get(userDetailsUrl + user.getId()));
// Now the result has the type we expected


	public UserApi {

		private HttpClient httpClient;

		Mono<User> findUser(String username) {
			String queryUrl = "http://my-api-address/users/" + username;

			return Mono.fromCallable(() -> httpClient.get(queryUrl)).
					flatMap(response -> {
						if (response.statusCode == 404) return Mono.error(new NotFoundException("User " + username + " not found"));
						else if (response.statusCode == 500) return Mono.error(new InternalServerErrorException());
						else if (response.statusCode != 200) return Mono.error(new Exception("Unknown error calling my-api"));
						return Mono.just(response.data);
					});
		}*/



}
