package com.example.deliverygo.WebSocket;

import com.example.deliverygo.model.Order;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.SynchronousSink;

@Component
@Log4j2
@NoArgsConstructor
class OrderCreatedEventPublisher implements ApplicationListener<OrderCreatedEvent>, Consumer<SynchronousSink<OrderCreatedEvent>> {

	//private Executor executor;
	private final BlockingQueue<OrderCreatedEvent> queue = new LinkedBlockingQueue<>();

	// <4>
	@Override
	public void onApplicationEvent(OrderCreatedEvent event) {
		this.queue.offer(event);
	}

	@Override
	public void accept(SynchronousSink<OrderCreatedEvent> sink) {

		try {
			log.info("FluxSink currentThread {}", Thread.currentThread().getName());
			OrderCreatedEvent event = queue.take();
			sink.next(event);
		} catch (InterruptedException e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}

	}

	public void OfferStart(String s) {
		this.queue.offer(new OrderCreatedEvent(Order.builder().id(s).build()));
	}

}
