package com.example.coffeeshopbackend.controller;

import com.example.coffeeshopbackend.entity.Order;
import com.example.coffeeshopbackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")  // THIS IS CRITICAL - Makes the endpoint /orders
@CrossOrigin(origins = "*")  // THIS IS CRITICAL - Allows frontend to connect
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // GET all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                return new ResponseEntity<>(order, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST - Create new order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            // Set the order date to now if not provided
            if (order.getOrderDate() == null) {
                order.setOrderDate(LocalDateTime.now());
            }

            // Save the order
            Order savedOrder = orderRepository.save(order);
            return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // This will help you see the error in console
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT - Update order
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                order.setCustomerName(orderDetails.getCustomerName());
                order.setCustomerEmail(orderDetails.getCustomerEmail());
                order.setCustomerPhone(orderDetails.getCustomerPhone());
                order.setTotalPrice(orderDetails.getTotalPrice());
                order.setStatus(orderDetails.getStatus());
                order.setDeliveryAddress(orderDetails.getDeliveryAddress());

                Order updatedOrder = orderRepository.save(order);
                return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE order
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable Long id) {
        try {
            orderRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}