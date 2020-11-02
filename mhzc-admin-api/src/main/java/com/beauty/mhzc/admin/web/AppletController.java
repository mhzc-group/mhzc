package com.beauty.mhzc.admin.web;

import com.beauty.mhzc.admin.annotation.RequiresPermissionsDesc;
import com.beauty.mhzc.core.util.ResponseUtil;
import com.beauty.mhzc.db.domain.Applet;
import com.beauty.mhzc.db.domain.Mall;
import com.beauty.mhzc.db.domain.Manager;
import com.beauty.mhzc.db.enums.ResultEnum;
import com.beauty.mhzc.db.service.AppletService;
import com.beauty.mhzc.db.service.impl.MallServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * @author xuan
 * @date 2020/11/1 15:55
 */

@RestController
@RequestMapping("/admin/applet")
@Api(value = "小程序控制器",tags = "小程序控制器")
@Validated
public class AppletController extends BaseController {

    @Autowired
    private AppletService appletService;
    @Autowired
    private MallServiceImpl mallService;

    @RequiresPermissions("admin:applet:create")
    @RequiresPermissionsDesc(menu = {"小程序控制器", "小程序控制器"}, button = "添加")
    @PostMapping("/create")
    @ApiOperation(value = "小程序信息添加", notes = "小程序信息添加，并与商城关联")
    @Transactional(propagation = Propagation.REQUIRED)
    public Object create(@Validated({Applet.InsertGroup.class})Applet applet, BindingResult bindingResult,@NotEmpty String mallId){
        if(bindingResult.hasErrors()){
            return ResponseUtil.fail(ResultEnum.PARAMETER_CANNOT_BE_EMPTY.getCode(),buildValiateMsg(bindingResult).toString());
        }
        //获取当前登陆用户信息
        Subject currentUser = SecurityUtils.getSubject();
        Manager currentAdmin= (Manager) currentUser.getPrincipal();
        applet.setCreateBy(currentAdmin.getUsername());
        applet.setUpdateBy(null);
        String appletId = appletService.save(applet);
        Mall mall=new Mall();
        mall.setId(mallId);
        mall.setAppId(appletId);
        mallService.modifyById(mall);
        return  ResponseUtil.ok();
    }

    @RequiresPermissions("admin:applet:update")
    @RequiresPermissionsDesc(menu = {"小程序控制器", "小程序控制器"}, button = "修改")
    @PostMapping("/update")
    @ApiOperation(value = "小程序信息修改", notes = "小程序信息修改")
    public Object update(@Validated({Applet.UpdateGroup.class})Applet applet, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseUtil.fail(ResultEnum.PARAMETER_CANNOT_BE_EMPTY.getCode(),buildValiateMsg(bindingResult).toString());
        }
        //获取当前登陆用户信息
        Subject currentUser = SecurityUtils.getSubject();
        Manager currentAdmin= (Manager) currentUser.getPrincipal();
        applet.setCreateBy(null);
        applet.setUpdateBy(currentAdmin.getUsername());
        appletService.modifyById(applet);
        return  ResponseUtil.ok();
    }

    @RequiresPermissions("admin:applet:read")
    @RequiresPermissionsDesc(menu = {"小程序控制器", "小程序控制器"}, button = "详情")
    @PostMapping("/read")
    @ApiOperation(value = "查询小程序信息详情", notes = "查询小程序信息详情")
    public Object read(@NotEmpty String mallId){
        Mall mall = mallService.queryById(mallId, null);
        Applet applet=new Applet();
        if(Objects.nonNull(mall)){
            String id=mall.getId();
            applet= appletService.queryById(id);
        }
        return  ResponseUtil.ok(applet);
    }
}
