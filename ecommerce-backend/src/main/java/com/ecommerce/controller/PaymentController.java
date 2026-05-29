package com.ecommerce.controller;

//import com.ecommerce.entity.Order;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import lombok.RequiredArgsConstructor;

import org.json.JSONObject;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayClient
            razorpayClient;

    @PostMapping("/create-order")
    public String createOrder(
            @RequestParam Double amount
    ) throws Exception {

        JSONObject options =
                new JSONObject();

        options.put(
                "amount",
                amount * 100
        );

        options.put(
                "currency",
                "INR"
        );

        options.put(
                "receipt",
                "txn_123456"
        );

        Order order =
                razorpayClient.orders
                        .create(options);

        return order.toString();
    }
}