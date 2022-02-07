package com.example.deliverygo.repository;

import com.example.deliverygo.entity.CryptoOauth2User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CryptoOauth2UserRepository extends ReactiveMongoRepository<CryptoOauth2User, String> {

}
