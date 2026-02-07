package com.example.coffeeshopbackend.service;

import com.example.coffeeshopbackend.entity.Order;
import com.example.coffeeshopbackend.entity.Order.OrderStatus;
import com.example.coffeeshopbackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public Order getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    // Create new order
    public Order createOrder(Order order) {
        // Set default values if not provided
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        return orderRepository.save(order);
    }

    // Update order
    public Order updateOrder(Long id, Order orderDetails) {
        Order order = getOrderById(id);

        if (orderDetails.getCustomerName() != null) {
            order.setCustomerName(orderDetails.getCustomerName());
        }
        if (orderDetails.getCustomerEmail() != null) {
            order.setCustomerEmail(orderDetails.getCustomerEmail());
        }
        if (orderDetails.getCustomerPhone() != null) {
            order.setCustomerPhone(orderDetails.getCustomerPhone());
        }
        if (orderDetails.getTotalPrice() != null) {
            order.setTotalPrice(orderDetails.getTotalPrice());
        }
        if (orderDetails.getStatus() != null) {
            order.setStatus(orderDetails.getStatus());
        }
        if (orderDetails.getDeliveryAddress() != null) {
            order.setDeliveryAddress(orderDetails.getDeliveryAddress());
        }
        if (orderDetails.getSpecialInstructions() != null) {
            order.setSpecialInstructions(orderDetails.getSpecialInstructions());
        }

        return orderRepository.save(order);
    }

    // Delete order
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }

    // Get orders by status
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // Get orders by customer email
    public List<Order> getOrdersByCustomerEmail(String email) {
        return orderRepository.findByCustomerEmail(email);
    }

    // Update order status
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}