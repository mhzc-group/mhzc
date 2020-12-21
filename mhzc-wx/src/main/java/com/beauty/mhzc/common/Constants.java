package com.beauty.mhzc.common;

/**
 * 全局常量
 * @author xuan
 * @date 2020/12/20 21:32
 */

public interface Constants {

    interface Jwt{

    }

    /**
     * Redis存储数据的key前缀
     */
    interface Redis {

        /**
         * 存储的token前缀
         * 完整为 前缀+wxOpenId
         */
        String JWT_TOKEN = "JWT-SESSION-";

    }

}
