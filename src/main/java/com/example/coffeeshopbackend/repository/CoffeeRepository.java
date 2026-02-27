package com.example.coffeeshopbackend.repository;

import com.example.coffeeshopbackend.entity.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {

    Optional<Coffee> findByName(String name);

    List<Coffee> findByCategory(String category);

    List<Coffee> findByAvailableTrue();

    List<Coffee> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Coffee c WHERE c.price BETWEEN :minPrice AND :maxPrice")
    List<Coffee> findByPriceRange(@Param("minPrice") Double minPrice,
                                  @Param("maxPrice") Double maxPrice);
}