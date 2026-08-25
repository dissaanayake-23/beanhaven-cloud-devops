package com.example.coffeeshopbackend;

import com.example.coffeeshopbackend.entity.Coffee;
import com.example.coffeeshopbackend.entity.Role;
import com.example.coffeeshopbackend.entity.User;
import com.example.coffeeshopbackend.repository.CoffeeRepository;
import com.example.coffeeshopbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // =====================================================
        // ADMIN ACCOUNT
        // =====================================================

        String adminPassword = System.getenv("ADMIN_PASSWORD");

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "ADMIN_PASSWORD environment variable is required"
            );
        }

        User admin = userRepository.findByUsername("admin")
                .orElseGet(User::new);

        admin.setUsername("admin");
        admin.setEmail("admin@beanhaven.com");

        // Password comes from environment variable
        // and is securely stored using BCrypt
        admin.setPassword(
                passwordEncoder.encode(adminPassword)
        );

        admin.setFullName("Bean Haven Administrator");
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);

        // Reset failed login attempts / account lock
        admin.setFailedAttempts(0);
        admin.setLockTime(null);

        userRepository.save(admin);

        System.out.println("==========================================");
        System.out.println("✅ ADMIN ACCOUNT READY");
        System.out.println("Username: admin");
        System.out.println("Admin password loaded securely.");
        System.out.println("Role: ADMIN");
        System.out.println("==========================================");


        // =====================================================
        // SAMPLE COFFEE DATA
        // =====================================================

        if (coffeeRepository.count() == 0) {

            System.out.println("Initializing sample coffee data...");


            // -------------------------------------------------
            // ESPRESSO
            // -------------------------------------------------

            Coffee espresso = new Coffee();

            espresso.setName("Classic Espresso");

            espresso.setDescription(
                    "A rich, bold espresso shot"
            );

            espresso.setPrice(3.50);
            espresso.setCategory("Espresso");
            espresso.setRoastType("Dark");
            espresso.setOrigin("Brazil");

            coffeeRepository.save(espresso);


            // -------------------------------------------------
            // CAPPUCCINO
            // -------------------------------------------------

            Coffee cappuccino = new Coffee();

            cappuccino.setName("Cappuccino");

            cappuccino.setDescription(
                    "Espresso with steamed milk and foam"
            );

            cappuccino.setPrice(4.25);
            cappuccino.setCategory("Cappuccino");
            cappuccino.setRoastType("Medium");
            cappuccino.setOrigin("Colombia");

            coffeeRepository.save(cappuccino);


            // -------------------------------------------------
            // LATTE
            // -------------------------------------------------

            Coffee latte = new Coffee();

            latte.setName("Caramel Latte");

            latte.setDescription(
                    "Latte with caramel syrup"
            );

            latte.setPrice(5.00);
            latte.setCategory("Latte");
            latte.setRoastType("Medium");
            latte.setOrigin("Ethiopia");

            coffeeRepository.save(latte);


            // -------------------------------------------------
            // AMERICANO
            // -------------------------------------------------

            Coffee americano = new Coffee();

            americano.setName("Iced Americano");

            americano.setDescription(
                    "Espresso with cold water and ice"
            );

            americano.setPrice(3.75);
            americano.setCategory("Iced Coffee");
            americano.setRoastType("Medium");
            americano.setOrigin("Guatemala");

            coffeeRepository.save(americano);


            // -------------------------------------------------
            // COLD BREW
            // -------------------------------------------------

            Coffee coldBrew = new Coffee();

            coldBrew.setName("Cold Brew");

            coldBrew.setDescription(
                    "Slow-steeped cold coffee"
            );

            coldBrew.setPrice(4.50);
            coldBrew.setCategory("Cold Brew");
            coldBrew.setRoastType("Light");
            coldBrew.setOrigin("Kenya");

            coffeeRepository.save(coldBrew);


            System.out.println(
                    "✅ Sample coffee data initialized!"
            );

        } else {

            System.out.println(
                    "✅ Database already contains "
                            + coffeeRepository.count()
                            + " coffees."
            );
        }
    }
}