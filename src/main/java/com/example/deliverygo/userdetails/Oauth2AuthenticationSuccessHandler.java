package com.example.deliverygo.userdetails;


import java.util.Map;
import lombok.RequiredArgsConstructor;
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

	//private final PortfolioCommandService portfolioService;
	//private final RedirectStrategy redirectStrategy;
	//private final UserRegistrationService userRegistrationService;

	@Override
	public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
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
		log.info(" Oauth2AuthenticationSuccessHandler : firstname {}, lastname {} , email {}, authentication.getName() {}", firstname, lastname,
				email, authentication.getName());
		return super.onAuthenticationSuccess(webFilterExchange, authentication);
	}

/**
 @Override public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
 Authentication authentication) throws IOException, ServletException {
 if(!this.portfolioService.userHasAportfolio(authentication.getName())) {
 this.portfolioService.createNewPortfolio(authentication.getName());
 OAuth2AuthenticationToken token = (OAuth2AuthenticationToken)authentication;
 Map<String, Object> attributes = token.getPrincipal().getAttributes();
 String firstname = null, lastname = null, email = null;
 if(token.getAuthorizedClientRegistrationId().equals("facebook")) {
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
 UserOAuth2Dto user = new UserOAuth2Dto(firstname,lastname,authentication.getName(),email);
 this.userRegistrationService.registerNewAuth2User(user);
 }

 this.redirectStrategy.sendRedirect(request, response, "/portfolio");
 }
 **/
}
