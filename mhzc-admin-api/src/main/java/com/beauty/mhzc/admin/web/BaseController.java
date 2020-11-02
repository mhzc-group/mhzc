package com.beauty.mhzc.admin.web;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Web MVC的控制器基类,提供一些通用功能
 * @author xuan
 * @date 2020/11/1 16:23
 */

public class BaseController {


    /**
     * 构建参数校验的错误信息
     *
     * @param bindingResult
     * @return
     */
    protected List<String> buildValiateMsg(Errors bindingResult) {
        List<String> errorMsg = new ArrayList<>();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            errorMsg.add(fieldError.getDefaultMessage());
        }
        return errorMsg;
    }

}
