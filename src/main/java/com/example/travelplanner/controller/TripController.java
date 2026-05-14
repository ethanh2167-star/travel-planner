package com.example.travelplanner.controller;

import com.example.travelplanner.entity.*;
import com.example.travelplanner.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    private final UserService userService;

    public TripController(TripService tripService, UserService userService) {
        this.tripService = tripService;
        this.userService = userService;
    }


    @GetMapping
    public String listTrips(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = userService.getByUsername(ud.getUsername());
        model.addAttribute("trips", tripService.getUserTrips(user));
        model.addAttribute("tripCount", tripService.countUserTrips(user));
        model.addAttribute("username", user.getFullName() != null ? user.getFullName() : user.getUsername());
        return "trips/list";
    }


    @GetMapping("/new")
    public String newTripForm() {
        return "trips/form";
    }


    @PostMapping
    public String createTrip(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam String title,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal budget,
            RedirectAttributes ra) {

        User user = userService.getByUsername(ud.getUsername());
        Trip trip = Trip.builder()
                .title(title).destination(destination)
                .startDate(startDate).endDate(endDate)
                .description(description).budget(budget)
                .build();
        Trip saved = tripService.createTrip(trip, user);
        ra.addFlashAttribute("successMsg", "行程已建立！");
        return "redirect:/trips/" + saved.getId();
    }


    @GetMapping("/{id}")
    public String viewTrip(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails ud,
                            Model model) {
        User user = userService.getByUsername(ud.getUsername());
        Trip trip = tripService.getTripById(id, user);


        System.out.println(">>> trip = " + trip);
        System.out.println(">>> items = " + trip.getItems());

        if (trip.getItems() == null) {
            trip.setItems(new ArrayList<>());
        }

        model.addAttribute("trip", trip);
        model.addAttribute("maxDay", trip.getDurationDays());
        return "trips/detail";
    }


    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails ud,
                            Model model) {
        User user = userService.getByUsername(ud.getUsername());
        model.addAttribute("trip", tripService.getTripById(id, user));
        return "trips/form";
    }


    @PostMapping("/{id}/update")
    public String updateTrip(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam String title, @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal budget,
            @RequestParam(required = false, defaultValue = "PLANNING") String status,
            RedirectAttributes ra) {

        User user = userService.getByUsername(ud.getUsername());
        Trip updated = Trip.builder()
                .title(title).destination(destination)
                .startDate(startDate).endDate(endDate)
                .description(description).budget(budget).status(status)
                .build();
        tripService.updateTrip(id, updated, user);
        ra.addFlashAttribute("successMsg", "行程已更新！");
        return "redirect:/trips/" + id;
    }


    @PostMapping("/{id}/delete")
    public String deleteTrip(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails ud,
                              RedirectAttributes ra) {
        User user = userService.getByUsername(ud.getUsername());
        tripService.deleteTrip(id, user);
        ra.addFlashAttribute("successMsg", "行程已刪除。");
        return "redirect:/trips";
    }


    @PostMapping("/{id}/items")
    public String addItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam int dayNumber,
            @RequestParam(required = false) String itemTime,
            @RequestParam String placeName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String note) {

        User user = userService.getByUsername(ud.getUsername());
        TripItem item = TripItem.builder()
                .dayNumber(dayNumber)
                .itemTime(itemTime != null && !itemTime.isBlank()
                        ? LocalTime.parse(itemTime) : null)
                .placeName(placeName)
                .category(category).note(note)
                .build();
        tripService.addItem(id, item, user);
        return "redirect:/trips/" + id;
    }


    @PostMapping("/{id}/items/{itemId}/delete")
    public String removeItem(@PathVariable Long id, @PathVariable Long itemId,
                              @AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByUsername(ud.getUsername());
        tripService.removeItem(id, itemId, user);
        return "redirect:/trips/" + id;
    }
}
