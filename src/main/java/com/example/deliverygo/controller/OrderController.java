package com.example.deliverygo.controller;

import com.example.deliverygo.WebSocket.OrderEvent;
import com.example.deliverygo.model.Order;
import com.example.deliverygo.model.OrderEventType;
import com.example.deliverygo.repository.OrderRepository;
import java.time.LocalDateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

	// You must set Order as capped document to make it work, this is just a POC
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<Order> getOrderStreaming() {
		return orderRepository.findWithTailableCursorBy();
	}

	@GetMapping("{id}")
	public Mono<ResponseEntity<Order>> getOrder(@PathVariable String id) {
		return orderRepository
				.findById(id)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Order> saveOrder(@RequestBody Order order) {
		return orderRepository
				.save(order)
				.doOnSuccess(orderCreated -> this.publisher.publishEvent
						(new OrderEvent(OrderEventType.builder().eventId(orderCreated.getId()).eventType("CREATE_ORDER").build())));

	}

	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Void>> deleteOrder(@PathVariable(value = "id") String id) {
		return orderRepository
				.findById(id)
				.flatMap(existingOrder -> orderRepository
								.delete(existingOrder)
					    	.doOnSuccess(notUsed -> this.publisher.publishEvent(new OrderEvent(OrderEventType.builder().eventId(id).eventType("DELETE_ORDER").build())))
								.then(Mono.just(ResponseEntity.ok().<Void>build()))
				)
				.defaultIfEmpty(ResponseEntity.notFound().build());
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
					existingProduct.setProposals(order.getProposals());
					return orderRepository.save(existingProduct);
				})
				.map(updateOrder -> {
					this.publisher.publishEvent(new OrderEvent(OrderEventType.builder().eventId(updateOrder.getId()).eventType("UPDATE_ORDER").build()));
					return ResponseEntity.ok(updateOrder);
				})
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

}
