package com.travel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

public class Order {
    private Integer id;
    private String orderNo;
    private Integer userId;
    private Integer ticketId;
    private Integer count;
    private Integer status;
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date travelDate;

    private Double totalPrice;

    // 出行人姓名，对应数据库 person_name
    private String personName;
    // 联系手机号
    private String phone;

    // 联表查询得到的门票名称，用于前端展示
    private String ticketName;

    // 订单类型：1 门票订单，2 酒店住宿订单
    // 注意：不要给默认值 1，否则查询时未传 orderType 会被强制按门票过滤，酒店订单查不出来
    private Integer orderType;
    // 酒店住宿订单对应的酒店 id（门票订单为 null）
    private Integer hotelId;
    // 联表查询得到的酒店名称（住宿订单展示用）
    private String hotelName;

    // 后台订单按创建时间筛选（yyyy-MM-dd，仅查询条件，不落库）
    private String startDate;
    private String endDate;
    // 查询“待出行”时是否排除已过期订单（仅查询条件，不落库）
    private Boolean excludeExpired;

    public Boolean getExcludeExpired() {
        return excludeExpired;
    }

    public void setExcludeExpired(Boolean excludeExpired) {
        this.excludeExpired = excludeExpired;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(Date travelDate) {
        this.travelDate = travelDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }
}
