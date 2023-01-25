package com.junior.mall.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.mall.exception.MallExceptionEnum;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseUtils<T> {
    private String status;
    private String msg;

    private T data;

    public ResponseUtils() {
        this.status = "10000";
        this.msg = "SUCCESS";
    }

    public ResponseUtils(T data) {
        this.status = "10000";
        this.msg = "SUCCESS";
        this.data = data;
    }

    public ResponseUtils(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ResponseUtils(String status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResponseUtils<T> success() {
        return new ResponseUtils<>();
    }

    public static <T> ResponseUtils<T> success(T result) {
        return new ResponseUtils<>(result);
    }

    public static ResponseUtils error(String status, String msg) {
        return new ResponseUtils(status, msg);
    }

    public static ResponseUtils error(MallExceptionEnum err) {
        return new ResponseUtils(err.getStatus(), err.getMsg());
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseUtils{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public String toJsonString() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        String json = null;
        try {
            json = objectMapper.writeValueAsString(this);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
