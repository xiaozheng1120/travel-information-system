package com.travel.entity;

import java.util.Date;

/**
 * 用户收藏（景点 / 攻略 / 酒店）
 */
public class Favorite {
    private Integer id;
    private Integer userId;
    // spot 景点 / guide 攻略 / hotel 住宿
    private String targetType;
    private Integer targetId;
    private String targetName;
    private String targetImg;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetImg() {
        return targetImg;
    }

    public void setTargetImg(String targetImg) {
        this.targetImg = targetImg;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
