package com.example.deliverygo.validation;

import com.example.deliverygo.repository.CryptoUserRepository;
import io.netty.util.internal.StringUtil;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

	private CryptoUserRepository cryptoUserRepository;

	@SneakyThrows
	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {

		if (!StringUtil.isNullOrEmpty(username)) {
			return cryptoUserRepository
					.findByEmail(username)
					.map(cryptoUser -> false)
					.defaultIfEmpty(true)
					.toFuture()
					.get();
		}

		return false;
	}

}
