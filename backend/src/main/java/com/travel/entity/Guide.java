package com.travel.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Guide {
    private Integer id;
    private String title;
    private String author;
    private String category;
    private String content;
    private String img;
    private String avatar;
    private Date date;
    // 新增：标记是否为固定初始化数据 1固定不可删改，0用户新增
    private Integer isFixed;
    // 新增：详情页浏览量、推荐指数
    private Integer views;
    private Double rating;
    // 新增：是否推荐到首页 1推荐 0普通
    private Integer isRecommend;
}