package com.beauty.mhzc.admin.web;

import com.beauty.mhzc.db.service.MaterialStorageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xuan
 * @date 2020/12/6 22:27
 */

@RestController
@RequestMapping("/admin/material_storage")
@Api(value = "素材文件管理控制器",tags = "素材文件管理控制器")
@Validated
public class MaterialStorageController {

    @Autowired
    private MaterialStorageService service;


}
