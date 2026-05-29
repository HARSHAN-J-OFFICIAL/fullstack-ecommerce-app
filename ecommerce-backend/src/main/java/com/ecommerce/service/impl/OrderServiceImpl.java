package com.ecommerce.service.impl;

import com.ecommerce.entity.*;

import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;

import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;

    private final OrderRepository orderRepository;

    private final CartService cartService;

    private final UserRepository userRepository;

    @Override
    public Order placeOrder() {

        // Get logged-in user email
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        // Get user from DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Get user's cart
        Cart cart = cartService.getCart();

        // Create new order
        Order order = new Order();

        order.setUser(user);

        order.setOrderDate(LocalDateTime.now());

        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();

        double total = 0;

        // Copy cart items -> order items
        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            if (product.getStockQuantity()
                    < cartItem.getQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            product.setStockQuantity(

                    product.getStockQuantity()
                            - cartItem.getQuantity()
            );

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProduct(cartItem.getProduct());

            orderItem.setQuantity(cartItem.getQuantity());

            orderItem.setPrice(
                    cartItem.getProduct().getPrice()
            );

            total += cartItem.getQuantity()
                    * cartItem.getProduct().getPrice();

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);

        order.setTotalAmount(total);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear cart after order placed
        cart.getItems().clear();
        cartRepository.save(cart);
        return savedOrder;
    }

    @Override
    public List<Order> getMyOrders() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return orderRepository.findByUser(user);
    }
}