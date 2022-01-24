package com.example.deliverygo.WebSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RestController
@Log4j2
//curl 'http://localhost:8080/sse/orders' -v --noproxy '*'  | python -m json.tool
public class ServerSentEventController {

	private final Flux<OrderEvent> events;
	private final ObjectMapper objectMapper;


	public ServerSentEventController(OrderCreatedEventPublisher eventPublisher, ObjectMapper objectMapper) {

		AtomicInteger atomicUserNbrConnected = new AtomicInteger(0);

		this.events = Flux
				.generate(eventPublisher)
				.share()
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
				.subscribeOn(Schedulers.fromExecutor(Executors.newWorkStealingPool()));

		this.objectMapper = objectMapper;
	}

	//curl 'http://localhost:8080/sse/orders' -v --noproxy '*'
	@GetMapping(path = "/sse/orders", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@CrossOrigin(origins = "http://localhost:3000")
	public Flux<String> profiles() {
		return this.events.map(pce -> {
			try {
				return objectMapper.writeValueAsString(pce) + "\n\n";
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
