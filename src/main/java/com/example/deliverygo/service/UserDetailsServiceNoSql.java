package com.example.deliverygo.service;

import com.example.deliverygo.entity.CryptoUser;
import com.example.deliverygo.model.Authorities;
import com.example.deliverygo.repository.CryptoUserRepository;
import com.example.deliverygo.userdetails.MFAUser;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceNoSql implements ReactiveUserDetailsService {

	private final CryptoUserRepository cryptoUserRepository;
	private static final boolean DEFAULT_ACC_NON_EXP = true;
	private static final boolean DEFAULT_CRED_NON_EXP = true;
	private static final boolean DEFAULT_ACC_NON_LOCKED = true;

	private List<GrantedAuthority> buildAuthorities(List<String> authorities) {
		List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>(1);
		for(String authority : authorities) {
			authList.add(new SimpleGrantedAuthority(authority));
		}
		return authList;
	}

	public UserDetails toUserDetails(CryptoUser cryptoUser) {
		List<String> authorities = new ArrayList<>();
		authorities.add(Authorities.ROLE_USER);
		MFAUser springUser = new MFAUser(
				cryptoUser.getUsername(),
				cryptoUser.getPassword(),
				cryptoUser.isVerified(),
				DEFAULT_ACC_NON_EXP,
				DEFAULT_CRED_NON_EXP,
				DEFAULT_ACC_NON_LOCKED,
				buildAuthorities(authorities));
		springUser.setFirstName(cryptoUser.getFirstName());
		springUser.setLastName(cryptoUser.getLastName());
		springUser.setEmail(cryptoUser.getEmail());
		return springUser;
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return cryptoUserRepository
				.findByUsername(username)
				.switchIfEmpty(Mono.defer(() -> { return Mono.error(new UsernameNotFoundException("User Not Found")); }))
				.map(this::toUserDetails);
	}
}
