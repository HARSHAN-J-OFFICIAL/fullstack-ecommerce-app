package com.ecommerce.service;

import com.ecommerce.entity.Order;

import java.util.List;

public interface OrderService {

    // Place new order
    Order placeOrder();

    // Get logged-in user's orders
    List<Order> getMyOrders();
}