package com.beauty.mhzc.admin.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.beauty.mhzc.admin.annotation.RequiresPermissionsDesc;
import com.beauty.mhzc.admin.util.AdminResponseCode;
import com.beauty.mhzc.admin.util.Permission;
import com.beauty.mhzc.admin.util.PermissionUtil;
import com.beauty.mhzc.admin.vo.PermVo;
import com.beauty.mhzc.core.util.JacksonUtil;
import com.beauty.mhzc.core.util.ResponseUtil;
import com.beauty.mhzc.core.validator.Order;
import com.beauty.mhzc.core.validator.Sort;
import com.beauty.mhzc.db.domain.Manager;
import com.beauty.mhzc.db.domain.Role;
import com.beauty.mhzc.db.service.AdminService;
import com.beauty.mhzc.db.service.PermissionService;
import com.beauty.mhzc.db.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.beauty.mhzc.admin.util.AdminResponseCode.ROLE_NAME_EXIST;
import static com.beauty.mhzc.admin.util.AdminResponseCode.ROLE_USER_EXIST;

@RestController
@RequestMapping("/admin/role")
@Validated
@Api(value = "角色管理控制器",tags = "角色管理控制器")
public class AdminRoleController {
    private final Log logger = LogFactory.getLog(AdminRoleController.class);

    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private AdminService adminService;

    @RequiresPermissions("admin:role:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色查询")
    @GetMapping("/list")
    @ApiOperation(value = "获取角色列表", notes = "获取角色列表;排序字段只支持add_time和id，排序类型支持desd和asc")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "角色名称", required = false, defaultValue = "", dataType = "string", paramType = "query"),
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
        List<Role> roleList = roleService.querySelective(name, page, limit, sort, order);
        return ResponseUtil.okList(roleList);
    }

    @RequiresAuthentication
    @GetMapping("/options")
    @ApiOperation(value = "获取角色下拉选择列表数据", notes = "获取角色下拉选择列表数据；value:label")
    public Object options() {
        List<Role> roleList = roleService.queryAll();

        List<Map<String, Object>> options = new ArrayList<>(roleList.size());
        for (Role role : roleList) {
            Map<String, Object> option = new HashMap<>(2);
            option.put("value", role.getId());
            option.put("label", role.getName());
            options.add(option);
        }

        return ResponseUtil.okList(options);
    }

    @RequiresPermissions("admin:role:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色详情")
    @GetMapping("/read")
    @ApiOperation(value = "查看角色详情", notes = "查看角色详情")
    @ApiImplicitParam(name = "id", value = "角色id", required = true, defaultValue = "", dataType = "integer", paramType = "query")
    public Object read(@NotNull Integer id) {
        Role role = roleService.findById(id);
        return ResponseUtil.ok(role);
    }


    private Object validate(Role role) {
        String name = role.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        return null;
    }

    @RequiresPermissions("admin:role:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色添加")
    @PostMapping("/create")
    @ApiOperation(value = "角色添加", notes = "角色添加")
    public Object create(@RequestBody Role role) {
        Object error = validate(role);
        if (error != null) {
            return error;
        }

        if (roleService.checkExist(role.getName())) {
            return ResponseUtil.fail(ROLE_NAME_EXIST, "角色已经存在");
        }

        roleService.add(role);

        return ResponseUtil.ok(role);
    }

    @RequiresPermissions("admin:role:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色编辑")
    @PostMapping("/update")
    @ApiOperation(value = "角色编辑", notes = "角色编辑")
    public Object update(@RequestBody Role role) {
        Object error = validate(role);
        if (error != null) {
            return error;
        }

        roleService.updateById(role);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:role:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色删除")
    @PostMapping("/delete")
    @ApiOperation(value = "角色删除", notes = "角色删除")
    public Object delete(@RequestBody Role role) {
        Integer id = role.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        // 如果当前角色所对应管理员仍存在，则拒绝删除角色。
        List<Manager> adminList = adminService.all();
        for (Manager admin : adminList) {
            Integer[] roleIds = admin.getRoleIds();
            for (Integer roleId : roleIds) {
                if (id.equals(roleId)) {
                    return ResponseUtil.fail(ROLE_USER_EXIST, "当前角色存在管理员，不能删除");
                }
            }
        }

        roleService.deleteById(id);
        return ResponseUtil.ok();
    }


    @Autowired
    private ApplicationContext context;
    private List<PermVo> systemPermissions = null;
    private Set<String> systemPermissionsString = null;

    private List<PermVo> getSystemPermissions() {
        final String basicPackage = "com.beauty.mhzc.admin";
        if (systemPermissions == null) {
            List<Permission> permissions = PermissionUtil.listPermission(context, basicPackage);
            systemPermissions = PermissionUtil.listPermVo(permissions);
            systemPermissionsString = PermissionUtil.listPermissionString(permissions);
        }
        return systemPermissions;
    }

    private Set<String> getAssignedPermissions(Integer roleId) {
        // 这里需要注意的是，如果存在超级权限*，那么这里需要转化成当前所有系统权限。
        // 之所以这么做，是因为前端不能识别超级权限，所以这里需要转换一下。
        Set<String> assignedPermissions = null;
        if (permissionService.checkSuperPermission(roleId)) {
            getSystemPermissions();
            assignedPermissions = systemPermissionsString;
        } else {
            assignedPermissions = permissionService.queryByRoleId(roleId);
        }

        return assignedPermissions;
    }

    /**
     * 管理员的权限情况
     *
     * @return 系统所有权限列表和管理员已分配权限
     */
    @RequiresPermissions("admin:role:permission:get")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "权限详情")
    @GetMapping("/permissions")
    @ApiOperation(value = "管理员的权限情况", notes = "系统所有权限列表和管理员已分配权限")
    @ApiImplicitParam(name = "roleId", value = "角色id", required = true, defaultValue = "", dataType = "integer", paramType = "query")
    public Object getPermissions(Integer roleId) {
        List<PermVo> systemPermissions = getSystemPermissions();
        Set<String> assignedPermissions = getAssignedPermissions(roleId);

        Map<String, Object> data = new HashMap<>();
        data.put("systemPermissions", systemPermissions);
        data.put("assignedPermissions", assignedPermissions);
        return ResponseUtil.ok(data);
    }


    /**
     * 更新管理员的权限
     *
     * @param body
     * @return
     */
    @RequiresPermissions("admin:role:permission:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "权限变更")
    @PostMapping("/permissions")
    @ApiOperation(value = "更新管理员的权限",notes = "更新管理员的权限;格式：{\"roleId\": \"\", \"permissions\": \"\"}")
    public Object updatePermissions(@RequestBody String body) {
        Integer roleId = JacksonUtil.parseInteger(body, "roleId");
        List<String> permissions = JacksonUtil.parseStringList(body, "permissions");
        if (roleId == null || permissions == null) {
            return ResponseUtil.badArgument();
        }

        // 如果修改的角色是超级权限，则拒绝修改。
        if (permissionService.checkSuperPermission(roleId)) {
            return ResponseUtil.fail(AdminResponseCode.ROLE_SUPER_SUPERMISSION, "当前角色的超级权限不能变更");
        }

        // 先删除旧的权限，再更新新的权限
        permissionService.deleteByRoleId(roleId);
        for (String permission : permissions) {
            com.beauty.mhzc.db.domain.Permission Permission = new com.beauty.mhzc.db.domain.Permission();
            Permission.setRoleId(roleId);
            Permission.setPermission(permission);
            permissionService.add(Permission);
        }
        return ResponseUtil.ok();
    }

}
