package com.example.deliverygo.repository;

import com.example.deliverygo.entity.CryptoOauth2User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CryptoOauth2UserRepository extends ReactiveMongoRepository<CryptoOauth2User, String> {
	Mono<CryptoOauth2User> findByUsername(String username);
	Mono<Boolean> existsByUsername(String username);

}
