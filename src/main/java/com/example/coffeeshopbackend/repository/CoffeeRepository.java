package com.example.coffeeshopbackend.repository;

import com.example.coffeeshopbackend.entity.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {

    // Find by category
    List<Coffee> findByCategory(String category);

    // Find available coffees
    List<Coffee> findByAvailableTrue();

    // Find by name containing (search)
    List<Coffee> findByNameContainingIgnoreCase(String name);

    // Find coffees in price range
    @Query("SELECT c FROM Coffee c WHERE c.price BETWEEN :minPrice AND :maxPrice")
    List<Coffee> findByPriceBetween(@Param("minPrice") Double minPrice,
                                    @Param("maxPrice") Double maxPrice);

    // Find by category and available
    List<Coffee> findByCategoryAndAvailableTrue(String category);
}