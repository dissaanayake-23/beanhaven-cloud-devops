package com.example.coffeeshopbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Name must be between 2 and 100 characters"
    )
    @Column(
            name = "customer_name",
            nullable = false
    )
    private String customerName;


    @Email(message = "Invalid email format")
    @Column(name = "customer_email")
    private String customerEmail;


    @Pattern(
            regexp = "^[0-9]{10,15}$",
            message = "Phone must be 10-15 digits"
    )
    @Column(name = "customer_phone")
    private String customerPhone;


    @Column(name = "order_date")
    private LocalDateTime orderDate =
            LocalDateTime.now();


    @NotNull(message = "Total price is required")
    @PositiveOrZero(message = "Price cannot be negative")
    @Column(
            name = "total_price",
            nullable = false
    )
    private Double totalPrice;


    // =========================================================
    // ORDER STATUS
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status =
            OrderStatus.PENDING;


    // =========================================================
    // ORDER TYPE
    // DELIVERY / TAKEAWAY
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType;


    // =========================================================
    // DELIVERY ADDRESS
    // For takeaway frontend sends "Takeaway"
    // =========================================================

    @NotBlank(message = "Delivery address is required")
    @Size(
            max = 500,
            message = "Address cannot exceed 500 characters"
    )
    @Column(
            name = "delivery_address",
            length = 500
    )
    private String deliveryAddress;


    @Column(
            name = "special_instructions",
            length = 1000
    )
    private String specialInstructions;


    // =========================================================
    // RELATIONSHIP WITH USER
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-orders")
    private User user;


    // =========================================================
    // RELATIONSHIP WITH ORDER ITEMS
    // =========================================================

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<OrderItem> orderItems;


    // =========================================================
    // ORDER STATUS ENUM
    // =========================================================

    public enum OrderStatus {

        PENDING,

        PREPARING,

        READY,

        OUT_FOR_DELIVERY,

        DELIVERED,

        CANCELLED
    }


    // =========================================================
    // ORDER TYPE ENUM
    // =========================================================

    public enum OrderType {

        DELIVERY,

        TAKEAWAY
    }


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public Order() {
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Order(
            String customerName,
            String customerEmail,
            String customerPhone,
            Double totalPrice,
            OrderStatus status,
            OrderType orderType,
            String deliveryAddress
    ) {

        this.customerName =
                customerName;

        this.customerEmail =
                customerEmail;

        this.customerPhone =
                customerPhone;

        this.totalPrice =
                totalPrice;

        this.status =
                status;

        this.orderType =
                orderType;

        this.deliveryAddress =
                deliveryAddress;

        this.orderDate =
                LocalDateTime.now();
    }


    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public Long getId() {

        return id;
    }


    public void setId(Long id) {

        this.id =
                id;
    }


    public String getCustomerName() {

        return customerName;
    }


    public void setCustomerName(
            String customerName
    ) {

        this.customerName =
                customerName;
    }


    public String getCustomerEmail() {

        return customerEmail;
    }


    public void setCustomerEmail(
            String customerEmail
    ) {

        this.customerEmail =
                customerEmail;
    }


    public String getCustomerPhone() {

        return customerPhone;
    }


    public void setCustomerPhone(
            String customerPhone
    ) {

        this.customerPhone =
                customerPhone;
    }


    public LocalDateTime getOrderDate() {

        return orderDate;
    }


    public void setOrderDate(
            LocalDateTime orderDate
    ) {

        this.orderDate =
                orderDate;
    }


    public Double getTotalPrice() {

        return totalPrice;
    }


    public void setTotalPrice(
            Double totalPrice
    ) {

        this.totalPrice =
                totalPrice;
    }


    public OrderStatus getStatus() {

        return status;
    }


    public void setStatus(
            OrderStatus status
    ) {

        this.status =
                status;
    }


    public OrderType getOrderType() {

        return orderType;
    }


    public void setOrderType(
            OrderType orderType
    ) {

        this.orderType =
                orderType;
    }


    public String getDeliveryAddress() {

        return deliveryAddress;
    }


    public void setDeliveryAddress(
            String deliveryAddress
    ) {

        this.deliveryAddress =
                deliveryAddress;
    }


    public String getSpecialInstructions() {

        return specialInstructions;
    }


    public void setSpecialInstructions(
            String specialInstructions
    ) {

        this.specialInstructions =
                specialInstructions;
    }


    public User getUser() {

        return user;
    }


    public void setUser(
            User user
    ) {

        this.user =
                user;
    }


    public List<OrderItem> getOrderItems() {

        return orderItems;
    }


    public void setOrderItems(
            List<OrderItem> orderItems
    ) {

        this.orderItems =
                orderItems;
    }
}