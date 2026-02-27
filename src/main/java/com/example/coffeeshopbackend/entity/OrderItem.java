package com.example.coffeeshopbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ CRITICAL FIX: Added @JsonBackReference to prevent circular reference
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "coffee_id", nullable = false)
    private Coffee coffee;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Price at time is required")
    @Min(value = 0, message = "Price cannot be negative")
    @Column(name = "price_at_time", nullable = false)
    private Double priceAtTime;

    @Column(name = "special_instructions", length = 500)
    private String specialInstructions;

    // Constructors
    public OrderItem() {}

    public OrderItem(Order order, Coffee coffee, Integer quantity, Double priceAtTime) {
        this.order = order;
        this.coffee = coffee;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Coffee getCoffee() { return coffee; }
    public void setCoffee(Coffee coffee) { this.coffee = coffee; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPriceAtTime() { return priceAtTime; }
    public void setPriceAtTime(Double priceAtTime) { this.priceAtTime = priceAtTime; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
}