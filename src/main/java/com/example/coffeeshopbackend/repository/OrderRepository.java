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
public interface OrderRepository
        extends JpaRepository<Order, Long> {


    // =========================================================
    // FIND ORDERS BY STATUS
    // =========================================================

    List<Order> findByStatus(
            OrderStatus status
    );


    // =========================================================
    // FIND ORDERS BY CUSTOMER EMAIL
    // =========================================================

    List<Order> findByCustomerEmail(
            String email
    );


    // =========================================================
    // FIND ORDERS BY CUSTOMER PHONE
    // Used for customer order tracking
    // =========================================================

    List<Order> findByCustomerPhone(
            String customerPhone
    );


    // =========================================================
    // FIND ORDERS BETWEEN DATES
    // =========================================================

    List<Order> findByOrderDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );


    // =========================================================
    // FIND ORDERS WITH TOTAL PRICE GREATER THAN
    // =========================================================

    List<Order> findByTotalPriceGreaterThan(
            Double price
    );


    // =========================================================
    // FIND ORDERS BY CUSTOMER NAME
    // CASE INSENSITIVE
    // =========================================================

    List<Order> findByCustomerNameContainingIgnoreCase(
            String name
    );


    // =========================================================
    // GET TOTAL REVENUE FROM DELIVERED ORDERS
    // =========================================================

    @Query(
            "SELECT SUM(o.totalPrice) " +
                    "FROM Order o " +
                    "WHERE o.status = 'DELIVERED'"
    )
    Double getTotalRevenue();


    // =========================================================
    // GET ORDERS WITH SPECIAL INSTRUCTIONS
    // =========================================================

    @Query(
            "SELECT o " +
                    "FROM Order o " +
                    "WHERE o.specialInstructions IS NOT NULL " +
                    "AND o.specialInstructions != ''"
    )
    List<Order> findOrdersWithSpecialInstructions();


    // =========================================================
    // GET RECENT ORDERS
    // =========================================================

    @Query(
            "SELECT o " +
                    "FROM Order o " +
                    "WHERE o.orderDate >= :date " +
                    "ORDER BY o.orderDate DESC"
    )
    List<Order> findRecentOrders(
            @Param("date")
            LocalDateTime date
    );
}