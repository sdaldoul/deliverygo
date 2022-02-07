package com.example.deliverygo.WebSocket;

import com.example.deliverygo.entity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Log4j2
@Configuration
/*
* wscat -c "ws://localhost:8080/ws/profiles"
* */
class WebSocketConfiguration {

	@Bean
	Executor executor() {
		return Executors.newWorkStealingPool();
	}

	@Bean
	HandlerMapping handlerMapping(WebSocketHandler wsh) {
		return new SimpleUrlHandlerMapping() {
			{
				setUrlMap(Collections.singletonMap("/ws/profiles", wsh));
				setOrder(10);
			}
		};
	}

	@Bean
	WebSocketHandlerAdapter webSocketHandlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

	@Bean
	WebSocketHandler webSocketHandler(ObjectMapper objectMapper, OrderCreatedEventPublisher eventPublisher, Executor executor) {

		AtomicInteger atomicUserNbrConnected = new AtomicInteger(0);

		Flux<OrderEvent> integerFlux = Flux.generate(eventPublisher).share();

		return session -> {

			Flux<WebSocketMessage> messageFlux = integerFlux
					.map(evt -> {
						try {
							Order profile = (Order) evt.getSource();
							Map<String, String> data = new HashMap<>();
							data.put("id", profile.getId());
							return objectMapper.writeValueAsString(data);
						} catch (JsonProcessingException e) {
							throw new RuntimeException(e);
						}
					})
					.map(str -> {
						log.info("sending " + str);
						return session.textMessage(str);
					})
					.doOnCancel(() -> {
						Integer integer;
						log.info("doOnCancel {}", integer = atomicUserNbrConnected.decrementAndGet());
						if (integer.intValue() == 0) {
							eventPublisher.OfferStart("END");
							log.info("SEND END {}", integer);
						}
					})
					.doFirst(() -> {
						Integer integerdoOnComplete;
						log.info("doFirst {}", integerdoOnComplete = atomicUserNbrConnected.incrementAndGet());
						if (integerdoOnComplete.intValue() == 1) {
							eventPublisher.OfferStart("START");
							log.info("SEND STRAT {}", integerdoOnComplete);
						}
					})
					.doOnComplete(() -> log.info("doOnComplete"))
					.subscribeOn(Schedulers.fromExecutor(executor));

			return session.send(messageFlux);
		};
	}
}
