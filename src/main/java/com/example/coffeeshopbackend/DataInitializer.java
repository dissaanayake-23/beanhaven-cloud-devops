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
        // Only initialize if database is empty
        if (coffeeRepository.count() == 0) {
            System.out.println("Initializing sample coffee data...");

            // Create sample coffees using setters
            Coffee espresso = new Coffee();
            espresso.setName("Classic Espresso");
            espresso.setDescription("A rich, bold espresso shot");
            espresso.setPrice(3.50);
            espresso.setCategory("Espresso");
            espresso.setRoastType("Dark");
            espresso.setOrigin("Brazil");
            coffeeRepository.save(espresso);

            Coffee cappuccino = new Coffee();
            cappuccino.setName("Cappuccino");
            cappuccino.setDescription("Espresso with steamed milk and foam");
            cappuccino.setPrice(4.25);
            cappuccino.setCategory("Cappuccino");
            cappuccino.setRoastType("Medium");
            cappuccino.setOrigin("Colombia");
            coffeeRepository.save(cappuccino);

            Coffee latte = new Coffee();
            latte.setName("Caramel Latte");
            latte.setDescription("Latte with caramel syrup");
            latte.setPrice(5.00);
            latte.setCategory("Latte");
            latte.setRoastType("Medium");
            latte.setOrigin("Ethiopia");
            coffeeRepository.save(latte);

            Coffee americano = new Coffee();
            americano.setName("Iced Americano");
            americano.setDescription("Espresso with cold water and ice");
            americano.setPrice(3.75);
            americano.setCategory("Iced Coffee");
            americano.setRoastType("Medium");
            americano.setOrigin("Guatemala");
            coffeeRepository.save(americano);

            Coffee coldBrew = new Coffee();
            coldBrew.setName("Cold Brew");
            coldBrew.setDescription("Slow-steeped cold coffee");
            coldBrew.setPrice(4.50);
            coldBrew.setCategory("Cold Brew");
            coldBrew.setRoastType("Light");
            coldBrew.setOrigin("Kenya");
            coffeeRepository.save(coldBrew);

            System.out.println("✅ Sample coffee data initialized!");
        } else {
            System.out.println("✅ Database already contains " + coffeeRepository.count() + " coffees.");
        }
    }
}