package com.beauty.mhzc.admin.web;

import com.beauty.mhzc.admin.annotation.RequiresPermissionsDesc;
import com.beauty.mhzc.admin.service.LogHelper;
import com.beauty.mhzc.core.util.ResponseUtil;
import com.beauty.mhzc.core.validator.Order;
import com.beauty.mhzc.core.validator.Sort;
import com.beauty.mhzc.db.domain.Manager;
import com.beauty.mhzc.db.domain.Material;
import com.beauty.mhzc.db.enums.ResultEnum;
import com.beauty.mhzc.db.service.MaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author xuan
 * @date 2020/11/2 21:19
 */

@RestController
@RequestMapping("/admin/material")
@Api(value = "素材管理控制器",tags = "素材管理控制器")
@Validated
public class MaterialController extends BaseController{
    @Autowired
    private LogHelper logHelper;
    @Autowired
    private MaterialService materialService;

    @RequiresPermissions("admin:material:create")
    @RequiresPermissionsDesc(menu = {"素材管理控制器", "素材管理控制器"}, button = "添加")
    @PostMapping("/create")
    @ApiOperation(value = "新增素材分组", notes = "新增素材分组")
    @ApiImplicitParam(name = "type", value = "分组类型 '0':picture;'1':video,默认：0", required = false, defaultValue = "", dataType = "string", paramType = "query")
    public Object create(@Validated({Material.InsertGroup.class})Material material, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseUtil.fail(ResultEnum.PARAMETER_CANNOT_BE_EMPTY.getCode(),buildValiateMsg(bindingResult).toString());
        }
        //获取当前登陆用户信息
        Subject currentUser = SecurityUtils.getSubject();
        Manager currentAdmin= (Manager) currentUser.getPrincipal();
        material.setCreateBy(currentAdmin.getUsername());
        material.setUpdateBy(null);
        materialService.save(material);
        return  ResponseUtil.ok();
    }

    @RequiresPermissions("admin:material:update")
    @RequiresPermissionsDesc(menu = {"素材管理控制器", "素材管理控制器"}, button = "修改")
    @PostMapping("/update")
    @ApiOperation(value = "修改素材分组", notes = "修改素材分组")
    public Object update(@Validated({Material.UpdateGroup.class})Material material, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseUtil.fail(ResultEnum.PARAMETER_CANNOT_BE_EMPTY.getCode(),buildValiateMsg(bindingResult).toString());
        }
        //获取当前登陆用户信息
        Subject currentUser = SecurityUtils.getSubject();
        Manager currentAdmin= (Manager) currentUser.getPrincipal();
        material.setCreateBy(null);
        material.setUpdateBy(currentAdmin.getUsername());
        materialService.modifyById(material);
        return  ResponseUtil.ok();
    }

    @RequiresPermissions("admin:material:list")
    @RequiresPermissionsDesc(menu = {"素材管理控制器", "素材管理控制器"}, button = "查询")
    @PostMapping("/list")
    @ApiOperation(value = "查询素材分组列表", notes = "查询素材分组列表,根据appId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "小程序appId", required = true, defaultValue = "", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, defaultValue = "1", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页条数", required = false, defaultValue = "10",dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序字段", required = false, defaultValue = "create_on",dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序类型", required = false, defaultValue = "desc",dataType = "string", paramType = "query")
    })
    public Object list(@NotEmpty String appId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "create_on") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order){
        List<Material> materials = materialService.queryList(appId);
        return  ResponseUtil.okList(materials);
    }


    @RequiresPermissions("admin:material:logicalDelete")
    @RequiresPermissionsDesc(menu = {"素材管理控制器", "素材管理控制器"}, button = "逻辑删除")
    @PostMapping("/logicalDelete")
    @ApiOperation(value = "逻辑删除素材分组", notes = "逻辑删除素材分组")
    public Object logicalDelete(@NotEmpty String id){
        Subject currentUser = SecurityUtils.getSubject();
        Manager currentAdmin= (Manager) currentUser.getPrincipal();
        //逻辑删除
        materialService.logicalDeleteById(id);
        logHelper.logAuthSucceed("逻辑删除素材分组", currentAdmin.getUsername());
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:material:delete")
    @RequiresPermissionsDesc(menu = {"素材管理控制器", "素材管理控制器"}, button = "物理删除")
    @PostMapping("/delete")
    @ApiOperation(value = "物理删除素材分组", notes = "物理删除素材分组")
    public Object delete(@NotEmpty String id){
        Subject currentUser = SecurityUtils.getSubject();
        Manager currentAdmin= (Manager) currentUser.getPrincipal();
        //逻辑删除
        materialService.deleteById(id);
        logHelper.logAuthSucceed("物理删除素材分组", currentAdmin.getUsername());
        return ResponseUtil.ok();
    }
}
