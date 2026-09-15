package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Orders", description = "Endpoints for order placement, tracking, and admin status management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place order", description = "Converts the user's active shopping cart into an Order with selected delivery address.")
    @PostMapping({"", "/place"})
    public Order placeOrder(
            @Parameter(description = "ID of selected delivery address") @RequestParam(required = false) Long addressId
    ) {
        return orderService.placeOrder(addressId);
    }

    @Operation(summary = "Get my orders", description = "Retrieves a list of orders placed by the logged-in user.")
    @GetMapping("/my-orders")
    public List<Order> getMyOrders() {
        return orderService.getMyOrders();
    }

    @Operation(summary = "Get order details by ID", description = "Retrieves itemized order details, payment status, and status history by order ID.")
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @Operation(summary = "View all orders (Admin)", description = "Retrieves a paginated list of all customer orders filtered by status or search term. Requires ROLE_ADMIN authority.")
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Order> getAllOrdersForAdmin(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getAllOrders(
                status,
                search,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"))
        );
    }

    @Operation(summary = "Update order status (Admin)", description = "Updates the status of an order (e.g. SHIPPED, DELIVERED). Requires ROLE_ADMIN authority.")
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Order updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody
    ) {
        String statusStr = requestBody.get("status");
        String comment = requestBody.get("comment");
        OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
        return orderService.updateOrderStatus(id, newStatus, comment);
    }
}