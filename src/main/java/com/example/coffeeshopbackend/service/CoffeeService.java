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

    public List<Coffee> getAllCoffees() {
        return coffeeRepository.findAll();
    }

    public Coffee getCoffeeById(Long id) {
        return coffeeRepository.findById(id).orElse(null);
    }

    public Coffee createCoffee(Coffee coffee) {
        return coffeeRepository.save(coffee);
    }

    public Coffee updateCoffee(Long id, Coffee coffeeDetails) {
        Coffee coffee = getCoffeeById(id);
        if (coffee == null) {
            return null;
        }

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

    public boolean deleteCoffee(Long id) {
        if (coffeeRepository.existsById(id)) {
            coffeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Coffee> getCoffeesByCategory(String category) {
        return coffeeRepository.findByCategory(category);
    }

    public List<Coffee> getAvailableCoffees() {
        return coffeeRepository.findByAvailableTrue();
    }

    public List<Coffee> searchCoffees(String keyword) {
        return coffeeRepository.findByNameContainingIgnoreCase(keyword);
    }
}