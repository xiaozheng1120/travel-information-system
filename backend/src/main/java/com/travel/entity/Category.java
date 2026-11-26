package com.travel.entity;
import lombok.Data;

@Data
public class Category {
    private Integer id;
    private String name;
    private Integer sort;
    private String status;
}