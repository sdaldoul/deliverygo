package com.example.deliverygo.model;

import com.example.deliverygo.validation.PasswordConfirmed;
import com.example.deliverygo.validation.PasswordPolicy;
import com.example.deliverygo.validation.UniqueEmail;
import com.example.deliverygo.validation.UniqueUsername;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@PasswordConfirmed
public class UserDto {

	@NotEmpty(message="Please enter your firstname")
	private String firstname;
	@NotEmpty(message="Please enter your lastname")
	private String lastname;
	@NotEmpty(message="Please enter a username")
	@UniqueUsername
	private String username;
	@NotEmpty(message="Please enter an email")
	@Email(message="Email is not valid")
	@UniqueEmail
	private String email;
	@NotEmpty(message="Please enter in a password")
	@PasswordPolicy
	private String password;
	@NotEmpty(message="Please confirm your password")
	private String confirmPassword;

}
