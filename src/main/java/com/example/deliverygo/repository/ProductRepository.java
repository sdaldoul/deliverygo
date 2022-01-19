package com.example.deliverygo.repository;

import com.example.deliverygo.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

	@Tailable
	Flux<Product> findWithTailableCursorBy();

}
