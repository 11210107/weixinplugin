package com.magic.xmagichooker.model;

public class BaseResult<T> {
    public static final int SUCCESS = 1;
    public static final int OBSOLETE = -4;

    private int code;
    private String message;
    private T data;
    private int total;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}