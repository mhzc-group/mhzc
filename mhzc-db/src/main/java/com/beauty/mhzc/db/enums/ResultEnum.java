package com.beauty.mhzc.db.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {
    FAILURE(-1, "失败"),
    SUCCESS(200, "成功"),
    PARAMETER_CANNOT_BE_EMPTY  (701,"参数不能为空"),
    SWITCH_AGAIN (702,"请重新切换商城"),
  ;
  private Integer code;
  private String msg;

  ResultEnum(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }


}
