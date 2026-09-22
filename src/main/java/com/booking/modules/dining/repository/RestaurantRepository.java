package com.booking.modules.dining.repository;

import com.booking.modules.dining.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByActiveTrueOrderByIdAsc();
    List<Restaurant> findByCategoryAndActiveTrue(String category);
    List<Restaurant> findByCityAndActiveTrue(String city);
}
