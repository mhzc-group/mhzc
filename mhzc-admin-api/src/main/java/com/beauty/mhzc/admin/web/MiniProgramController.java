package com.beauty.mhzc.admin.web;

import com.beauty.mhzc.admin.annotation.RequiresPermissionsDesc;
import com.beauty.mhzc.core.util.ResponseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xuan
 * @date 2020/10/3 16:51
 */

@RestController
@RequestMapping("/admin/mini")
@Validated
public class MiniProgramController {

    private final Log logger = LogFactory.getLog(MiniProgramController.class);

    @RequiresPermissions("admin:mini:list")
    @RequiresPermissionsDesc(menu = {"小程序管理", "小程序配置"}, button = "查询")
    @GetMapping("/list")
    public Object list(String name, String content,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {

        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:mini:product")
    @RequiresPermissionsDesc(menu = {"商品管理", "商品管理"}, button = "查询")
    @GetMapping("/product")
    public Object product(String name, String content,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {

        return ResponseUtil.ok();
    }
}
