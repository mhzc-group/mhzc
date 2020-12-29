package com.beauty.mhzc.wx.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.beauty.mhzc.wx.utils.ResponseUtil;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 辅助统一异常处理，获取filter抛出异常
 * @author xuan
 * @date 2020/12/23 23:23
 */

@Controller
public class CustomErrorController extends BasicErrorController {

    private static final TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {
    };

    public CustomErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes, new ErrorProperties());
    }

    @RequestMapping
    @ResponseBody
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        final Map<String, Object> body = getErrorAttributes(request, false);

        final HttpStatus status = getStatus(request);
        return
                new ResponseEntity<>(
                        JSON.parseObject(JSON.toJSONString(ResponseUtil.fail(status)),
                                mapTypeReference
                        ),
                        status
                );

    }

}