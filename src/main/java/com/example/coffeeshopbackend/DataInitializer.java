package com.example.coffeeshopbackend;

import com.example.coffeeshopbackend.entity.Coffee;
import com.example.coffeeshopbackend.repository.CoffeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only add sample data if no coffees exist
        if (coffeeRepository.count() == 0) {
            System.out.println("🚀 Adding sample coffee data...");

            // Coffee 1: Espresso
            Coffee espresso = new Coffee(
                    "Espresso",
                    "Strong coffee shot",
                    3.50,
                    "Hot",
                    "Dark",
                    "Italy"
            );
            coffeeRepository.save(espresso);

            // Coffee 2: Cappuccino
            Coffee cappuccino = new Coffee(
                    "Cappuccino",
                    "Coffee with milk foam",
                    4.50,
                    "Hot",
                    "Medium",
                    "Italy"
            );
            coffeeRepository.save(cappuccino);

            // Coffee 3: Latte
            Coffee latte = new Coffee(
                    "Latte",
                    "Coffee with steamed milk",
                    5.00,
                    "Hot",
                    "Medium",
                    "USA"
            );
            coffeeRepository.save(latte);

            System.out.println("✅ Added sample coffees to database!");
        }
    }
}