package com.ecommerce.controller;

import com.ecommerce.entity.Order;

import com.ecommerce.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Place order
    @PostMapping("/place")
    public Order placeOrder() {

        return orderService.placeOrder();
    }

    // Get logged-in user's orders
    @GetMapping("/my-orders")
    public List<Order> getMyOrders() {

        return orderService.getMyOrders();
    }
}