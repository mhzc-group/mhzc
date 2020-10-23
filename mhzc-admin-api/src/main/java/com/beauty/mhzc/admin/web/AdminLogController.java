package com.beauty.mhzc.admin.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.beauty.mhzc.admin.annotation.RequiresPermissionsDesc;
import com.beauty.mhzc.core.util.ResponseUtil;
import com.beauty.mhzc.core.validator.Order;
import com.beauty.mhzc.core.validator.Sort;
import com.beauty.mhzc.db.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/log")
@Validated
@Api(value = "日志控制器",tags = "日志控制器")
public class AdminLogController {
    private final Log logger = LogFactory.getLog(AdminLogController.class);

    @Autowired
    private LogService logService;

    @RequiresPermissions("admin:log:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "操作日志"}, button = "查询")
    @GetMapping("/list")
    @ApiOperation(value = "获取系统操作日志", notes = "获取系统操作日志;排序字段只支持add_time和id，排序类型支持desd和asc")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "操作人账号", required = false, defaultValue = "", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, defaultValue = "1", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页条数", required = false, defaultValue = "10",dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序字段", required = false, defaultValue = "add_time",dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序类型", required = false, defaultValue = "desc",dataType = "string", paramType = "query")
    })
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {

        List<com.beauty.mhzc.db.domain.Log> logList = logService.querySelective(name, page, limit, sort, order);
        return ResponseUtil.okList(logList);
    }
}
