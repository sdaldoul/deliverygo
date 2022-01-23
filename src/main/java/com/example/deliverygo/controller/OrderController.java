package com.example.deliverygo.controller;

import com.example.deliverygo.WebSocket.OrderCreatedEvent;
import com.example.deliverygo.model.Order;
import com.example.deliverygo.repository.OrderRepository;
import java.time.LocalDateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderRepository orderRepository;
	private final ApplicationEventPublisher publisher;

	public OrderController(OrderRepository orderRepository, ApplicationEventPublisher publisher) {
		this.orderRepository = orderRepository;
		this.publisher = publisher;
	}

	@GetMapping
	public Flux<Order> getAllProducts() {
		return orderRepository.findAll();
	}

	// You must set Order as capped document to make it work
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<Order> getOrderStreaming() {
		return orderRepository.findWithTailableCursorBy();
	}

	@GetMapping("{id}")
	public Mono<ResponseEntity<Order>> getOrder(@PathVariable String id) {
		return orderRepository.findById(id).map(product -> ResponseEntity.ok(product)).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Order> saveOrder(@RequestBody Order order) {
		return orderRepository.
				save(order).
				doOnSuccess(orderCreated -> this.publisher.publishEvent(new OrderCreatedEvent(orderCreated)));

	}

	@PutMapping("{id}")
	public Mono<ResponseEntity<Order>> updateOrder(@PathVariable(value = "id") String id, @RequestBody Order order) {
		return orderRepository
				.findById(id)
				.flatMap(existingProduct -> {
					existingProduct.setOrderUpdateDateTime(LocalDateTime.now());
					existingProduct.setProductName(order.getProductName());
					existingProduct.setReadyToPay(order.getReadyToPay());
					existingProduct.setCityToCollect(order.getCityToCollect());
					return orderRepository.save(existingProduct);
				})
				.map(updateProduct -> ResponseEntity.ok(updateProduct))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

}
