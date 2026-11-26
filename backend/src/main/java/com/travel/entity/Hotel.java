package com.travel.entity;

import lombok.Data;

@Data
public class Hotel {
    private Integer id;
    private String name;
    private Integer spotId;
    private String nearSpot;
    private String distance;
    private String type;
    private Integer typeId;
    private String address;
    private String priceRange;
    private Double score;
    private String desc;
    private String img;
}