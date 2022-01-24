package com.example.deliverygo.WebSocket;

import com.example.deliverygo.model.Order;
import com.example.deliverygo.model.OrderEventType;
import org.springframework.context.ApplicationEvent;

public class OrderEvent extends ApplicationEvent {

    public OrderEvent(OrderEventType source) {
        super(source);
    }
}
