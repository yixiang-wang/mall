package com.junior.mall.service.exception;

import com.junior.mall.exception.MallExceptionEnum;

public class CategoryException extends Exception {
    private final String status;
    private final String msg;

    public CategoryException(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public CategoryException(MallExceptionEnum err) {
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
