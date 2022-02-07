package com.example.deliverygo.repository;

import com.example.deliverygo.entity.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

	@Tailable
	Flux<Order> findWithTailableCursorBy();

}
