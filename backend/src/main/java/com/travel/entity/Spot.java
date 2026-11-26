package com.travel.entity;

import lombok.Data;

@Data
public class Spot {
    private Integer id;
    private String name;
    private String area;
    private Double price;
    private Integer categoryId;
    private String categoryName;
    private String desc;
    private String img;
    private Integer status;
    // 新增数据库description字段，用于详情页长文本介绍
    private String description;
    // 新增：详情页信息卡字段（开放时间 / 建议游玩时长 / 景点等级 / 标签，逗号分隔）
    private String openTime;
    private String playTime;
    private String level;
    private String tags;
}