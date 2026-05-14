package com.example.travelplanner.service;

import com.example.travelplanner.entity.*;
import com.example.travelplanner.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> getUserTrips(User user) {
        return tripRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Trip getTripById(Long id, User user) {
        return tripRepository.findByIdAndUserWithItems(id, user)
                .orElseThrow(() -> new RuntimeException("行程不存在或無權限"));
    }

    @Transactional
    public Trip createTrip(Trip trip, User user) {
        trip.setUser(user);
        return tripRepository.save(trip);
    }

    @Transactional
    public Trip updateTrip(Long id, Trip updated, User user) {
        Trip trip = getTripById(id, user);
        trip.setTitle(updated.getTitle());
        trip.setDestination(updated.getDestination());
        trip.setStartDate(updated.getStartDate());
        trip.setEndDate(updated.getEndDate());
        trip.setDescription(updated.getDescription());
        trip.setBudget(updated.getBudget());
        trip.setStatus(updated.getStatus());
        return tripRepository.save(trip);
    }

    @Transactional
    public void deleteTrip(Long id, User user) {
        Trip trip = getTripById(id, user);
        tripRepository.delete(trip);
    }


    @Transactional
    public Trip addItem(Long tripId, TripItem item, User user) {
        Trip trip = getTripById(tripId, user);
        item.setTrip(trip);
        trip.getItems().add(item);
        return tripRepository.save(trip);
    }

  
    @Transactional
    public Trip removeItem(Long tripId, Long itemId, User user) {
        Trip trip = getTripById(tripId, user);
        trip.getItems().removeIf(i -> i.getId().equals(itemId));
        return tripRepository.save(trip);
    }

    public long countUserTrips(User user) {
        return tripRepository.countByUser(user);
    }
}
