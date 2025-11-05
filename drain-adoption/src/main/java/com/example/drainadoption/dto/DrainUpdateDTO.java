package com.example.drainadoption.dto;

import lombok.Data;

@Data
public class DrainUpdateDTO {
    private String name;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
}