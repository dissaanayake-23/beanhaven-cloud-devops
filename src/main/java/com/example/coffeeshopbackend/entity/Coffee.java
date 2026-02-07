package com.example.coffeeshopbackend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "coffees")
public class Coffee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "category")
    private String category;

    @Column(name = "roast_type")
    private String roastType;

    @Column(name = "origin")
    private String origin;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "available", nullable = false)
    private Boolean available = true;

    @OneToMany(mappedBy = "coffee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    public Coffee() {}

    public Coffee(String name, String description, Double price, String category,
                  String roastType, String origin) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.roastType = roastType;
        this.origin = origin;
        this.available = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getRoastType() { return roastType; }
    public void setRoastType(String roastType) { this.roastType = roastType; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}