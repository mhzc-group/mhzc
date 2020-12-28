package com.beauty.mhzc.wx.vo;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author xuan
 * @date 2020/12/20 20:43
 */

public class JwtToken implements AuthenticationToken {
    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
