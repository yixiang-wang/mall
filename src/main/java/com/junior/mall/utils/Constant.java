package com.junior.mall.utils;

import com.junior.mall.exception.MallExceptionEnum;
import com.junior.mall.service.exception.ProductException;
import com.sun.tools.jdi.InterfaceTypeImpl;
import io.lettuce.core.internal.LettuceSets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class Constant {
    public static String FILE_UPLOAD_DIR;

    public static String EMAIL_FROM="max2010ps@163.com";

    public final static String JWT_KEY="mall";
    public final static String USER_NAME="username";
    public final static String USER_ID="userId";
    public final static String USER_ROLE="userRole";
    public final static Long EXPIRE_TIME=1000*60*60*24*10L;
    public final static String JWT_TOKEN="jwt_token";

    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    public interface ProductListOrderBy {
        Set<String> PRICE_ORDER_ENUM = LettuceSets.newHashSet("price desc", "price asc");
    }

    public interface SaleStatus {
        Integer SALE = 1;
        Integer NOT_SALE = 0;
    }

    public interface CartSelect {
        Integer SELECT = 1;
        Integer NOT_SELECT = 0;
    }

    public enum OrderStatusName {
        CANCELED(0, "用户已取消"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40, "交易完成");

        Integer code;
        String value;

        OrderStatusName(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        public static OrderStatusName getStatus(Integer code) throws ProductException {
            for (OrderStatusName orderStatusName : values()) {
                if (orderStatusName.getCode().equals(code)) {
                    return orderStatusName;
                }
            }
            throw new ProductException(MallExceptionEnum.NOT_FOUND);
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
