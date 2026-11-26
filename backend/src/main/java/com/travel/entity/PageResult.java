package com.travel.entity;

import java.util.List;

public class PageResult<T> {
    private List<T> list;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;

    // 无参构造（给收藏模块用，new PageResult<>() 然后set）
    public PageResult(){}

    // 双参构造（给景点、攻略分页用，直接new PageResult<>(list,total)）
    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    // ==========新增这个4参数构造器==========
    public PageResult(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
