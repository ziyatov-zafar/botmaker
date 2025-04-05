package org.example.newbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,columnDefinition = "TEXT",name = "adreeess")
    private String address;
    private Double latitude;
    private Double longitude;
    private Long userId;
    private Boolean active;
}
