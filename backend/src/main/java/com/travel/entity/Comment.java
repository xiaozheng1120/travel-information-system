package com.travel.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private Integer id;
    private Integer guideId;
    private Integer userId;
    private String username;
    private String avatar;
    private String content;
    private Double rating;
    private Date createTime;
}
