package com.junior.mall.exception;

public enum MallExceptionEnum {
    NEED_USER_NAME("10001", "用户名不能为空"),
    NEED_PASSWORD("10002", "密码不能为空"),
    PASSWORD_TOO_SHORT("10003", "密码长度不能小于8位"),
    NAME_EXISTED("10004", "不允许重名"),
    INSERT_FAILED("10005", "插入失败，请重试"),
    WRONG_PASSWORD("10006", "密码错误"),
    NEED_LOGIN("10007", "用户未登录"),
    UPDATE_FAILED("10008", "更新失败"),
    NEED_ADMIN("10009", "无管理员权限"),
    SYSTEM_ERROR("20000", "系统异常，请从控制台或日志中查看具体错误信息"),
    NAME_NOT_EXISTED("10010", "用户不存在，请先注册"),
    NOT_FOUND("10011", "未找到指定内容，请重试"),
    MKDIR_FAILED("10013", "文件夹创建失败"),
    NOT_SALE("10014", "商品未上架"),
    NOT_ENOUGH("10015", "商品库存不足"),
    NOT_YOUR_ORDER("10017", "订单归属不符"),
    WRONG_ORDER_STATUS("10018", "错误的订单状态"),
    EMAIL_ADDRESS_ILLOGICAL("10019", "不合法的邮箱"),
    EMAIL_ADDRESS_EXISTED("10020", "重复注册的邮箱"),
    EMAIL_ALREADY_SEND("10021", "重复注册的邮箱"),
    NEED_EMAIL_ADDRESS("10022", "邮箱不能为空"),
    NEED_VERIFICATION_CODE("10023", "验证码不能为空"),
    WRONG_CODE("10024", "验证码错误"),
    TOKEN_WRONG("10025", "解码失败"),
    TOKEN_EXPIRED("10026", "TOKEN过期"),

    NO_ORDER("10016", "订单不存在");


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

    String status;
    String msg;

    MallExceptionEnum(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
