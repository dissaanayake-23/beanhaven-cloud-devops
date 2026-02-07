package com.example.coffeeshopbackend.service;

import com.example.coffeeshopbackend.entity.Coffee;
import com.example.coffeeshopbackend.repository.CoffeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoffeeService {

    @Autowired
    private CoffeeRepository coffeeRepository;

    // Get all coffees
    public List<Coffee> getAllCoffees() {
        return coffeeRepository.findAll();
    }

    // Get coffee by ID
    public Coffee getCoffeeById(Long id) {
        Optional<Coffee> coffee = coffeeRepository.findById(id);
        return coffee.orElseThrow(() -> new RuntimeException("Coffee not found with id: " + id));
    }

    // Create new coffee
    public Coffee createCoffee(Coffee coffee) {
        return coffeeRepository.save(coffee);
    }

    // Update coffee
    public Coffee updateCoffee(Long id, Coffee coffeeDetails) {
        Coffee coffee = getCoffeeById(id);

        if (coffeeDetails.getName() != null) {
            coffee.setName(coffeeDetails.getName());
        }
        if (coffeeDetails.getDescription() != null) {
            coffee.setDescription(coffeeDetails.getDescription());
        }
        if (coffeeDetails.getPrice() != null) {
            coffee.setPrice(coffeeDetails.getPrice());
        }
        if (coffeeDetails.getCategory() != null) {
            coffee.setCategory(coffeeDetails.getCategory());
        }
        if (coffeeDetails.getRoastType() != null) {
            coffee.setRoastType(coffeeDetails.getRoastType());
        }
        if (coffeeDetails.getOrigin() != null) {
            coffee.setOrigin(coffeeDetails.getOrigin());
        }
        if (coffeeDetails.getImageUrl() != null) {
            coffee.setImageUrl(coffeeDetails.getImageUrl());
        }
        if (coffeeDetails.getAvailable() != null) {
            coffee.setAvailable(coffeeDetails.getAvailable());
        }

        return coffeeRepository.save(coffee);
    }

    // Delete coffee
    public void deleteCoffee(Long id) {
        Coffee coffee = getCoffeeById(id);
        coffeeRepository.delete(coffee);
    }

    // Get coffees by category
    public List<Coffee> getCoffeesByCategory(String category) {
        return coffeeRepository.findByCategory(category);
    }

    // Get available coffees
    public List<Coffee> getAvailableCoffees() {
        return coffeeRepository.findByAvailableTrue();
    }

    // Search coffees by name
    public List<Coffee> searchCoffees(String keyword) {
        return coffeeRepository.findByNameContainingIgnoreCase(keyword);
    }
}