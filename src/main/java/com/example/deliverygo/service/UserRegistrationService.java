package com.example.deliverygo.service;

import com.example.deliverygo.entity.CryptoUser;
import com.example.deliverygo.model.UserDto;
import com.example.deliverygo.repository.CryptoUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserRegistrationService {

	private final CryptoUserRepository repository;
	private final PasswordEncoder encoder;
	//private final Oauth2UserRepository oauth2Repository;

	public Mono<CryptoUser> registerNewUser(UserDto user) {
		CryptoUser cryptUser = new CryptoUser(
				user.getUsername(),
				user.getFirstname(),
				user.getLastname(),
				user.getEmail(),
				encoder.encode(user.getPassword()),
				encoder.encode(String.valueOf(user.getSecurityPin()))
		);
		cryptUser.setVerified(true);
		return repository
				.save(cryptUser)
				.doOnSuccess(cryptoUser -> log.info(cryptoUser))
				.doOnError(throwable -> log.error(throwable.getMessage()));
	}

	/*public void registerNewAuth2User(UserOAuth2Dto userDto) {
		CryptoOauth2User user = new CryptoOauth2User(userDto.getUsername(),
													 userDto.getFirstname(),
													 userDto.getLastname(),
													 userDto.getEmail());
		oauth2Repository.save(user);
	}*/

}
