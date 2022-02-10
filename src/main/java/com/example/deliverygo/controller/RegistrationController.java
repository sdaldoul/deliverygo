package com.example.deliverygo.controller;

import com.example.deliverygo.model.UserDto;
import com.example.deliverygo.service.UserRegistrationService;
//import javax.validation.Valid;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

	private final UserRegistrationService registrationService;
	//private final PortfolioCommandService portfolioService;

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("user",new UserDto());
		return "register";
	}

	@PostMapping("/register")
	public Mono<String> register(@Validated  @ModelAttribute("user") UserDto user/*, BindingResult result*/) {
		/*if(result.hasErrors()) {
			return "register";
		}*/

		/*Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<UserDto>> violations = validator.validate( user );
    log.info(violations.toString());
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}*/
		return registrationService
				.registerNewUser(user)
				.flatMap(cryptoUser -> Mono.just("redirect:register?success"))
				.onErrorReturn("register")
				;
		//this.portfolioService.createNewPortfolio(user.getUsername());

		//return Mono.just("redirect:register?success");
	}

}
