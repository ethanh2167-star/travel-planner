package com.example.travelplanner.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "trip_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "day_number", nullable = false)
    @Builder.Default
    private Integer dayNumber = 1;

    @Column(name = "item_time")
    private LocalTime itemTime;

    @Column(name = "place_name", nullable = false)
    private String placeName;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}