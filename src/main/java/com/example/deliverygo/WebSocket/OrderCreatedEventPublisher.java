package com.example.deliverygo.WebSocket;

import com.example.deliverygo.model.Order;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

@Component
@Log4j2
class OrderCreatedEventPublisher implements ApplicationListener<OrderCreatedEvent>, Consumer<FluxSink<OrderCreatedEvent>> {

	private final Executor executor;
	private final BlockingQueue<OrderCreatedEvent> queue = new LinkedBlockingQueue<>();

	OrderCreatedEventPublisher(Executor executor) {
		this.executor = executor;
		// IT is mandatory to set up at least one value in the queue, so u can get all records and second to fix refresh issue on the browser
		//refresh issue on the browser : I have to call two time to make it work without start value http://localhost:8080/ --> ws://localhost:8080/ws/profiles
		this.queue.offer(new OrderCreatedEvent(Order.builder().id("START").build()));
	}

	// <4>
	@Override
	public void onApplicationEvent(OrderCreatedEvent event) {
		this.queue.offer(event);
	}

	@Override
	public void accept(FluxSink<OrderCreatedEvent> sink) {

		this.executor.execute(() -> {
			while (true) {
				try {
					OrderCreatedEvent event = queue.take(); // <5>
					sink.next(event); // <6>
				} catch (InterruptedException e) {
					ReflectionUtils.rethrowRuntimeException(e);
				}
			}
		});
	}
}
