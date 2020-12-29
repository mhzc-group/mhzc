package com.beauty.mhzc.wx.utils;

import com.github.pagehelper.Page;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 响应操作结果
 * <pre>
 *  {
 *      errno： 错误码，
 *      errmsg：错误消息，
 *      data：  响应数据
 *  }
 * </pre>
 *
 * <p>
 * 错误码：
 * 参照 HttpStatus
 */
@Builder
public class ResponseUtil {
    public static Object ok() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", HttpStatus.OK.value());
        obj.put("errmsg", HttpStatus.OK.getReasonPhrase());
        return obj;
    }

    public static Object ok(Object data) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", HttpStatus.OK.value());
        obj.put("errmsg", HttpStatus.OK.getReasonPhrase());
        obj.put("data", data);
        return obj;
    }

    public static Object okList(List list) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list", list);

        if (list instanceof Page) {
            Page page = (Page) list;
            data.put("total", page.getTotal());
            data.put("page", page.getPageNum());
            data.put("limit", page.getPageSize());
            data.put("pages", page.getPages());
        } else {
            data.put("total", list.size());
            data.put("page", 1);
            data.put("limit", list.size());
            data.put("pages", 1);
        }

        return ok(data);
    }

    public static Object okList(List list, List pagedList) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list", list);

        if (pagedList instanceof Page) {
            Page page = (Page) pagedList;
            data.put("total", page.getTotal());
            data.put("page", page.getPageNum());
            data.put("limit", page.getPageSize());
            data.put("pages", page.getPages());
        } else {
            data.put("total", pagedList.size());
            data.put("page", 1);
            data.put("limit", pagedList.size());
            data.put("pages", 1);
        }

        return ok(data);
    }

    public static Object fail() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", HttpStatus.NOT_FOUND.value());
        obj.put("errmsg", HttpStatus.NOT_FOUND.getReasonPhrase());
        return obj;
    }

    public static Object fail(int errno, String errmsg) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", errno);
        obj.put("errmsg", errmsg);
        return obj;
    }

    public static Object fail(HttpStatus status) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno",status.value());
        obj.put("errmsg",status.getReasonPhrase());
        return obj;
    }

    public static Object unlogin() {
        return fail(512, "请登录");
    }

    public static Object unauthz() {
        return fail(HttpStatus.UNAUTHORIZED);
    }
}
