package com.example.deliverygo.validation;

import com.example.deliverygo.repository.CryptoUserRepository;
import io.netty.util.internal.StringUtil;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@Log4j2
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

	private CryptoUserRepository cryptoUserRepository;

	@SneakyThrows
	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {

		if (!StringUtil.isNullOrEmpty(email)) {
			return cryptoUserRepository
					.findByEmail(email)
					.map(cryptoUser -> false)
					.defaultIfEmpty(true)
					.toFuture()
					.get();
		}

		return false;

	}

}
