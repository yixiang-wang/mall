package com.junior.mall.service.exception;

import com.junior.mall.exception.MallExceptionEnum;

public class UserException extends Exception {
    private final String status;
    private final String msg;

    public UserException(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public UserException(MallExceptionEnum err) {
        this(err.getStatus(), err.getMsg());
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "UserException{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
