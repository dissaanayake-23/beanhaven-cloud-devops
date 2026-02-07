package com.example.coffeeshopbackend.repository;

import com.example.coffeeshopbackend.entity.Order;
import com.example.coffeeshopbackend.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ✅ Find orders by status
    List<Order> findByStatus(OrderStatus status);

    // ✅ Find orders by customer email
    List<Order> findByCustomerEmail(String email);

    // ✅ Find orders between dates
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    // ✅ Find orders with total price greater than
    List<Order> findByTotalPriceGreaterThan(Double price);

    // ✅ Find orders by customer name (case-insensitive)
    List<Order> findByCustomerNameContainingIgnoreCase(String name);

    // ✅ Advanced query: Get total revenue from delivered orders
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'DELIVERED'")
    Double getTotalRevenue();

    // ✅ Advanced query: Get orders with special instructions
    @Query("SELECT o FROM Order o WHERE o.specialInstructions IS NOT NULL AND o.specialInstructions != ''")
    List<Order> findOrdersWithSpecialInstructions();

    // ✅ Advanced query: Get recent orders (last 7 days)
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :date ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("date") LocalDateTime date);
}