package com.example.coffeeshopbackend.controller;

import com.example.coffeeshopbackend.entity.Coffee;
import com.example.coffeeshopbackend.service.CoffeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coffees")
@CrossOrigin(origins = "*")
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService;

    // GET ALL COFFEES
    @GetMapping
    public ResponseEntity<?> getAllCoffees() {
        try {
            List<Coffee> coffees = coffeeService.getAllCoffees();
            return ResponseEntity.ok(coffees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch coffees: " + e.getMessage()));
        }
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCoffeeById(@PathVariable Long id) {
        try {
            Coffee coffee = coffeeService.getCoffeeById(id);
            if (coffee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Coffee not found with id: " + id));
            }
            return ResponseEntity.ok(coffee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // CREATE COFFEE
    @PostMapping
    public ResponseEntity<?> createCoffee(@RequestBody Coffee coffee) {
        try {
            // Validation
            if (coffee.getName() == null || coffee.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Coffee name is required"));
            }
            if (coffee.getPrice() == null || coffee.getPrice() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Valid price is required (greater than 0)"));
            }

            Coffee savedCoffee = coffeeService.createCoffee(coffee);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCoffee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create coffee: " + e.getMessage()));
        }
    }

    // UPDATE COFFEE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoffee(@PathVariable Long id, @RequestBody Coffee coffeeDetails) {
        try {
            Coffee existingCoffee = coffeeService.getCoffeeById(id);
            if (existingCoffee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Coffee not found with id: " + id));
            }

            Coffee updatedCoffee = coffeeService.updateCoffee(id, coffeeDetails);
            return ResponseEntity.ok(updatedCoffee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update coffee: " + e.getMessage()));
        }
    }

    // DELETE COFFEE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoffee(@PathVariable Long id) {
        try {
            Coffee existingCoffee = coffeeService.getCoffeeById(id);
            if (existingCoffee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Coffee not found with id: " + id));
            }

            boolean deleted = coffeeService.deleteCoffee(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                        "message", "Coffee deleted successfully",
                        "deletedId", id,
                        "deletedName", existingCoffee.getName()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to delete coffee"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete coffee: " + e.getMessage()));
        }
    }

    // TEST ENDPOINT
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Coffee API is working!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "Bean Haven Coffee Shop API");
        return ResponseEntity.ok(response);
    }

    // HEALTH CHECK
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Coffee Shop Backend");
        response.put("timestamp", System.currentTimeMillis());

        try {
            List<Coffee> coffees = coffeeService.getAllCoffees();
            response.put("coffeeCount", coffees.size());
            response.put("database", "Connected");
        } catch (Exception e) {
            response.put("database", "Error: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    // GET BY CATEGORY
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getCoffeesByCategory(@PathVariable String category) {
        try {
            List<Coffee> coffees = coffeeService.getCoffeesByCategory(category);
            return ResponseEntity.ok(coffees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // GET AVAILABLE COFFEES
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableCoffees() {
        try {
            List<Coffee> coffees = coffeeService.getAvailableCoffees();
            return ResponseEntity.ok(coffees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // SEARCH COFFEES
    @GetMapping("/search")
    public ResponseEntity<?> searchCoffees(@RequestParam String keyword) {
        try {
            List<Coffee> coffees = coffeeService.searchCoffees(keyword);
            return ResponseEntity.ok(coffees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}