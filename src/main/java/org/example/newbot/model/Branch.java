package org.example.newbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Branch {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private Long botId;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String destination;//mo'ljal
    private String workingHours;
    private String phone;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private Boolean hasImage;
    private String status;
    private Boolean active;
}
