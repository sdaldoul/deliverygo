package com.example.deliverygo.repository;

import com.example.deliverygo.entity.CryptoUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CryptoUserRepository extends ReactiveMongoRepository<CryptoUser, String> {

	Mono<CryptoUser> findByUsername(String username);
	//CryptoUser findByEmail(String email);

}
