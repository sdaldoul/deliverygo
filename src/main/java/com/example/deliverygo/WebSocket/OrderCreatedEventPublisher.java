package com.example.deliverygo.WebSocket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

@Component
class OrderCreatedEventPublisher implements
    ApplicationListener<OrderCreatedEvent>, // <1>
    Consumer<FluxSink<OrderCreatedEvent>> { //<2>

    private final Executor executor;
    private final BlockingQueue<OrderCreatedEvent> queue =
        new LinkedBlockingQueue<>(); // <3>

    OrderCreatedEventPublisher(Executor executor) {
        this.executor = executor;
    }

    // <4>
    @Override
    public void onApplicationEvent(OrderCreatedEvent event) {
        this.queue.offer(event);
    }

     @Override
    public void accept(FluxSink<OrderCreatedEvent> sink) {
        this.executor.execute(() -> {
            while (true)
                try {
                    OrderCreatedEvent event = queue.take(); // <5>
                    sink.next(event); // <6>
                }
                catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }
}
