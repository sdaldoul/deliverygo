package com.example.deliverygo.WebSocket;

import com.example.deliverygo.model.Order;
import org.springframework.context.ApplicationEvent;

public class OrderCreatedEvent extends ApplicationEvent {

    public OrderCreatedEvent(Order source) {
        super(source);
    }
}
