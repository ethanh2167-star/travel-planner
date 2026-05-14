package com.example.travelplanner.controller;

import com.example.travelplanner.entity.User;
import com.example.travelplanner.entity.Trip;
import com.example.travelplanner.service.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/trips")
public class PdfController {

    private final TripService tripService;
    private final UserService userService;
    private final PdfExportService pdfExportService;

    public PdfController(TripService tripService, UserService userService,
                          PdfExportService pdfExportService) {
        this.tripService = tripService;
        this.userService = userService;
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud) {

        User user = userService.getByUsername(ud.getUsername());
        Trip trip = tripService.getTripById(id, user);
        byte[] pdf = pdfExportService.generateTripPdf(trip);

        
        String filename = "trip_" + trip.getId() + "_" + trip.getDestination()
                .replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", "_") + ".pdf";
        String encodedFilename = new String(filename.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.ISO_8859_1);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFilename + "\"")
                .body(pdf);
    }
}
