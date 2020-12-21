package com.beauty.mhzc.vo;

import lombok.Data;

/**
 * @author xuan
 * @date 2020/12/21 22:02
 */

@Data
public class Code2SessionResponse {
    private String openid;
    private String session_key;
    private String unionid;
    private String errcode = "0";
    private String errmsg;
    private int expires_in;
}
