package com.example.coffeeshopbackend.service;

import com.example.coffeeshopbackend.entity.OrderItem;
import com.example.coffeeshopbackend.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + id));
    }

    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public OrderItem updateOrderItem(Long id, OrderItem orderItemDetails) {
        OrderItem orderItem = getOrderItemById(id);

        orderItem.setOrder(orderItemDetails.getOrder());
        orderItem.setCoffee(orderItemDetails.getCoffee());
        orderItem.setQuantity(orderItemDetails.getQuantity());
        orderItem.setPriceAtTime(orderItemDetails.getPriceAtTime());
        orderItem.setSpecialInstructions(orderItemDetails.getSpecialInstructions());

        return orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(Long id) {
        OrderItem orderItem = getOrderItemById(id);
        orderItemRepository.delete(orderItem);
    }

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public List<OrderItem> getOrderItemsByCoffeeId(Long coffeeId) {
        return orderItemRepository.findByCoffeeId(coffeeId);
    }
}