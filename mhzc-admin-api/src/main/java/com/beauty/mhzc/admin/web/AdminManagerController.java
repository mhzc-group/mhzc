package com.beauty.mhzc.admin.web;

import com.beauty.mhzc.admin.dto.Select2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import com.beauty.mhzc.admin.annotation.RequiresPermissionsDesc;
import com.beauty.mhzc.admin.service.LogHelper;
import com.beauty.mhzc.core.util.RegexUtil;
import com.beauty.mhzc.core.util.ResponseUtil;
import com.beauty.mhzc.core.util.bcrypt.BCryptPasswordEncoder;
import com.beauty.mhzc.core.validator.Order;
import com.beauty.mhzc.core.validator.Sort;
import com.beauty.mhzc.db.domain.Manager;
import com.beauty.mhzc.db.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.beauty.mhzc.admin.util.AdminResponseCode.*;

@RestController
@RequestMapping("/admin/admin")
@Validated
@Api(value = "商户控制器（管理员控制器）",tags = "商户控制器（管理员控制器)")
public class AdminManagerController {
    private final Log logger = LogFactory.getLog(AdminManagerController.class);

    @Autowired
    private AdminService adminService;
    @Autowired
    private LogHelper logHelper;

    @RequiresPermissions("admin:admin:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "查询")
    @GetMapping("/list")
    @ApiOperation(value = "获取管理员列表", notes = "获取管理员列表;排序字段只支持add_time和id，排序类型支持desd和asc")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "操作人账号", required = false, defaultValue = "", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, defaultValue = "1", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页条数", required = false, defaultValue = "10",dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序字段", required = false, defaultValue = "add_time",dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序类型", required = false, defaultValue = "desc",dataType = "string", paramType = "query")
    })
    public Object list(String username,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<Manager> adminList = adminService.querySelective(username, page, limit, sort, order);
        return ResponseUtil.okList(adminList);
    }

    private Object validate(Manager admin) {
        String name = admin.getUsername();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isUsername(name)) {
            return ResponseUtil.fail(ADMIN_INVALID_NAME, "管理员名称不符合规定");
        }
        String password = admin.getPassword();
        if (StringUtils.isEmpty(password) || password.length() < 6) {
            return ResponseUtil.fail(ADMIN_INVALID_PASSWORD, "管理员密码长度不能小于6");
        }
        return null;
    }

    @RequiresPermissions("admin:admin:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "添加")
    @PostMapping("/create")
    @ApiOperation(value = "添加管理员", notes = "添加管理员")
    public Object create(@RequestBody Manager admin) {
        Object error = validate(admin);
        if (error != null) {
            return error;
        }

        String username = admin.getUsername();
        List<Manager> adminList = adminService.findAdmin(username);
        if (adminList.size() > 0) {
            return ResponseUtil.fail(ADMIN_NAME_EXIST, "管理员已经存在");
        }

        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);
        adminService.add(admin);
        logHelper.logAuthSucceed("添加管理员", username);
        return ResponseUtil.ok(admin);
    }

    @RequiresPermissions("admin:admin:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "详情")
    @GetMapping("/read")
    @ApiOperation(value = "查看管理员信息详情", notes = "查看管理员信息详情")
    @ApiImplicitParam(name = "id", value = "管理员账户id（不是账号）", required = true, defaultValue = "", dataType = "integer", paramType = "query")
    public Object read(@NotNull Integer id) {
        Manager admin = adminService.findById(id);
        return ResponseUtil.ok(admin);
    }

    @RequiresPermissions("admin:admin:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "编辑")
    @PostMapping("/update")
    @ApiOperation(value = "更新管理员信息", notes = "更新管理员信息")
    public Object update(@RequestBody Manager admin) {
        Object error = validate(admin);
        if (error != null) {
            return error;
        }

        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return ResponseUtil.badArgument();
        }

        // 不允许管理员通过编辑接口修改密码
        admin.setPassword(null);

        if (adminService.updateById(admin) == 0) {
            return ResponseUtil.updatedDataFailed();
        }

        logHelper.logAuthSucceed("编辑管理员", admin.getUsername());
        return ResponseUtil.ok(admin);
    }

    @RequiresPermissions("admin:admin:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "删除")
    @PostMapping("/delete")
    @ApiOperation(value = "删除管理员", notes = "删除管理员")
    public Object delete(@RequestBody Manager admin) {
        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return ResponseUtil.badArgument();
        }

        // 管理员不能删除自身账号
        Subject currentUser = SecurityUtils.getSubject();
        Manager currentAdmin = (Manager) currentUser.getPrincipal();
        if (currentAdmin.getId().equals(anotherAdminId)) {
            return ResponseUtil.fail(ADMIN_DELETE_NOT_ALLOWED, "管理员不能删除自己账号");
        }

        adminService.deleteById(anotherAdminId);
        logHelper.logAuthSucceed("删除管理员", admin.getUsername());
        return ResponseUtil.ok();
    }


}
