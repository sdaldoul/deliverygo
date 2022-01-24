package com.example.deliverygo.WebSocket;

import com.example.deliverygo.model.Order;
import org.springframework.context.ApplicationEvent;

public class OrderEvent extends ApplicationEvent {

    public OrderEvent(Order source) {
        super(source);
    }
}
