package com.travel.entity;

public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static Result success(Object data){
        Result r = new Result();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }
    public static Result success(){
        Result r = new Result();
        r.setCode(200);
        r.setMsg("操作成功");
        return r;
    }
    //新增这个error方法，解决当前红报错
    public static Result error(String msg){
        Result r = new Result();
        r.setCode(400);
        r.setMsg(msg);
        return r;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
