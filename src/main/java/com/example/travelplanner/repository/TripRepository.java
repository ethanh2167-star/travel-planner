package com.example.travelplanner.repository;

import com.example.travelplanner.entity.Trip;
import com.example.travelplanner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByUserOrderByCreatedAtDesc(User user);

    Optional<Trip> findByIdAndUser(Long id, User user);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.items WHERE t.id = :id AND t.user = :user")
    Optional<Trip> findByIdAndUserWithItems(@Param("id") Long id, @Param("user") User user);

    long countByUser(User user);
}
