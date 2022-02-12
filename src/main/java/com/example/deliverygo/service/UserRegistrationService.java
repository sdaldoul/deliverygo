package com.example.deliverygo.service;

import com.example.deliverygo.entity.CryptoOauth2User;
import com.example.deliverygo.entity.CryptoUser;
import com.example.deliverygo.model.UserDto;
import com.example.deliverygo.model.UserOAuth2Dto;
import com.example.deliverygo.repository.CryptoOauth2UserRepository;
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
	private final CryptoOauth2UserRepository cryptoOauth2UserRepository;

	public Mono<CryptoUser> registerNewUser(UserDto user) {
		CryptoUser cryptUser = new CryptoUser(
				user.getUsername(),
				user.getFirstname(),
				user.getLastname(),
				user.getEmail(),
				encoder.encode(user.getPassword())
		);
		cryptUser.setVerified(true);
		return repository
				.save(cryptUser)
				.doOnSuccess(cryptoUser -> log.info(cryptoUser))
				.doOnError(throwable -> log.error(throwable.getMessage()));
	}

	public Mono<CryptoOauth2User> registerNewAuth2User(UserOAuth2Dto userDto) {
		CryptoOauth2User user = new CryptoOauth2User(userDto.getUsername(),
													 userDto.getFirstname(),
													 userDto.getLastname(),
													 userDto.getEmail());
		return cryptoOauth2UserRepository.save(user);
	}

}
