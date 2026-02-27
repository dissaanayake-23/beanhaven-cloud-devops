package com.example.coffeeshopbackend.controller;

import com.example.coffeeshopbackend.entity.OrderItem;
import com.example.coffeeshopbackend.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")  // NO /api/ prefix!
@CrossOrigin("*")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    // Get all order items
    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.getAllOrderItems();
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    // Get order item by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        OrderItem orderItem = orderItemService.getOrderItemById(id);
        return new ResponseEntity<>(orderItem, HttpStatus.OK);
    }

    // Create new order item
    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItem orderItem) {
        System.out.println("Received order item: " + orderItem); // Debug log
        OrderItem createdOrderItem = orderItemService.createOrderItem(orderItem);
        return new ResponseEntity<>(createdOrderItem, HttpStatus.CREATED);
    }

    // Update order item
    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id,
                                                     @RequestBody OrderItem orderItemDetails) {
        OrderItem updatedOrderItem = orderItemService.updateOrderItem(id, orderItemDetails);
        return new ResponseEntity<>(updatedOrderItem, HttpStatus.OK);
    }

    // Delete order item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get order items by order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    // Get order items by coffee ID
    @GetMapping("/coffee/{coffeeId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByCoffeeId(@PathVariable Long coffeeId) {
        List<OrderItem> orderItems = orderItemService.getOrderItemsByCoffeeId(coffeeId);
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }
}