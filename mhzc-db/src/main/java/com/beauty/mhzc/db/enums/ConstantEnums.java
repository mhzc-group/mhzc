package com.beauty.mhzc.db.enums;

import lombok.Getter;

/**
 * @author xuan
 * @date 2020/10/21 17:02
 */

@Getter
public enum ConstantEnums {

     ADMINISTRATOR(0,"管理员"),
     MERCHANT (1,"商户");

    private Integer code;
    private String msg;

    ConstantEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
