package com.example.deliverygo.WebSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
public class ServerSentEventController {

	/*private final Flux<OrderCreatedEvent> events;
	private final ObjectMapper objectMapper;


	public ServerSentEventController(OrderCreatedEventPublisher eventPublisher, ObjectMapper objectMapper) {
		this.events = Flux.create(eventPublisher).share();
		this.objectMapper = objectMapper;
	}

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
	}*/
}
