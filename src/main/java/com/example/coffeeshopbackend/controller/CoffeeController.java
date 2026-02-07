package com.example.coffeeshopbackend.controller;

import com.example.coffeeshopbackend.entity.Coffee;
import com.example.coffeeshopbackend.service.CoffeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coffees")
@CrossOrigin("*")
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;

    // Get all coffees
    @GetMapping
    public ResponseEntity<List<Coffee>> getAllCoffees() {
        List<Coffee> coffees = coffeeService.getAllCoffees();
        return new ResponseEntity<>(coffees, HttpStatus.OK);
    }

    // Get coffee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Coffee> getCoffeeById(@PathVariable Long id) {
        Coffee coffee = coffeeService.getCoffeeById(id);
        return new ResponseEntity<>(coffee, HttpStatus.OK);
    }

    // Create new coffee (REMOVED @Valid)
    @PostMapping
    public ResponseEntity<Coffee> createCoffee(@RequestBody Coffee coffee) {
        Coffee createdCoffee = coffeeService.createCoffee(coffee);
        return new ResponseEntity<>(createdCoffee, HttpStatus.CREATED);
    }

    // Update coffee (REMOVED @Valid)
    @PutMapping("/{id}")
    public ResponseEntity<Coffee> updateCoffee(@PathVariable Long id,
                                               @RequestBody Coffee coffeeDetails) {
        Coffee updatedCoffee = coffeeService.updateCoffee(id, coffeeDetails);
        return new ResponseEntity<>(updatedCoffee, HttpStatus.OK);
    }

    // Delete coffee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoffee(@PathVariable Long id) {
        coffeeService.deleteCoffee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get coffees by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Coffee>> getCoffeesByCategory(@PathVariable String category) {
        List<Coffee> coffees = coffeeService.getCoffeesByCategory(category);
        return new ResponseEntity<>(coffees, HttpStatus.OK);
    }

    // Get available coffees
    @GetMapping("/available")
    public ResponseEntity<List<Coffee>> getAvailableCoffees() {
        List<Coffee> coffees = coffeeService.getAvailableCoffees();
        return new ResponseEntity<>(coffees, HttpStatus.OK);
    }

    // Search coffees
    @GetMapping("/search")
    public ResponseEntity<List<Coffee>> searchCoffees(@RequestParam String keyword) {
        List<Coffee> coffees = coffeeService.searchCoffees(keyword);
        return new ResponseEntity<>(coffees, HttpStatus.OK);
    }
}