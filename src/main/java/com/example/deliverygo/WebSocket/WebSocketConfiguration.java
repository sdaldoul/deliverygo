package com.example.deliverygo.WebSocket;

import com.example.deliverygo.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;

@Log4j2
@Configuration
class WebSocketConfiguration implements ApplicationListener<OrderCreatedEvent> {

	private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
	// <1>
	@Bean
	Executor executor() {
		return Executors.newSingleThreadExecutor();
	}

	// <2>
	@Bean
	HandlerMapping handlerMapping(WebSocketHandler wsh) {
		return new SimpleUrlHandlerMapping() {
			{
				// <3>
				setUrlMap(Collections.singletonMap("/ws/profiles", wsh));
				setOrder(10);
			}
		};
	}

	// <4>
	@Bean
	WebSocketHandlerAdapter webSocketHandlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

	@Bean
	WebSocketHandler webSocketHandler(ObjectMapper objectMapper, // <5>
			OrderCreatedEventPublisherSi eventPublisher // <6>
	) {
		log.info( "OUTSIDE WebSocketMessage currentThread {}", Thread.currentThread().getName());
		//////////////////////////////////////////////////////////////////////////////////

		AtomicInteger atomicUserNbrConnected = new AtomicInteger(0);

		//Flux generate sequence
/*		Flux<String> integerFlux = Flux.generate((SynchronousSink<String> synchronousSink) -> {
			log.info("Flux SynchronousSink generate {}", Thread.currentThread().getName());
			try {
				synchronousSink.next(queue.take());
			} catch (InterruptedException e) {
				ReflectionUtils.rethrowRuntimeException(e);
			}
		}).share();*/


		Flux<OrderCreatedEvent> integerFlux = Flux.generate(eventPublisher).share(); // <7>

		return session -> {

			log.info( "WebSocketMessage currentThread {}", Thread.currentThread().getName());

			Flux<WebSocketMessage> messageFlux = integerFlux
					.map(evt -> {
						try {
							Order profile = (Order) evt.getSource(); // <1>
							Map<String, String> data = new HashMap<>(); // <2>
							data.put("id", profile.getId());
							log.info( "INSIDE SEND WebSocketMessage currentThread {}", Thread.currentThread().getName());
							return objectMapper.writeValueAsString(data);
							// <3>
						} catch (JsonProcessingException e) {
							throw new RuntimeException(e);
						}
					})
					.map(str -> {
						log.info("sending " + str);
						return session.textMessage(str);
					})
					.doOnCancel( () -> {
						Integer integer;
						log.info("doOnCancel {}", integer = atomicUserNbrConnected.decrementAndGet());
						if (integer.intValue() == 0){
							eventPublisher.OfferStart("START");
							//this.queue.offer("START");
							log.info("SEND STRAT {}", integer);
						}
					})
					.doOnComplete(() -> log.info("doOnComplete"))
					.doFirst(() -> {
						log.info("doFirst {}", atomicUserNbrConnected.incrementAndGet());
					})
					.subscribeOn(Schedulers.fromExecutor(Executors.newWorkStealingPool()));

			return session.send(messageFlux);
		};
	}

	// <4>
	@Override
	public void onApplicationEvent(OrderCreatedEvent event) {
		 Order profile = (Order) event.getSource();
		 this.queue.offer(profile.getId());
	}
}
