package com.beauty.mhzc.admin.web;

import com.beauty.mhzc.admin.annotation.RequiresPermissionsDesc;
import com.beauty.mhzc.admin.dto.Select2;
import com.beauty.mhzc.admin.service.LogHelper;
import com.beauty.mhzc.core.util.ResponseUtil;
import com.beauty.mhzc.core.validator.Order;
import com.beauty.mhzc.core.validator.Sort;
import com.beauty.mhzc.db.domain.MallManager;
import com.beauty.mhzc.db.domain.Manager;
import com.beauty.mhzc.db.service.AdminService;
import com.beauty.mhzc.db.service.MallManagerService;
import com.beauty.mhzc.db.service.impl.AdminMallServiceImpl;
import com.beauty.mhzc.db.util.IdHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商城用户关系控制器
 * @author xuan
 * @date 2020/10/20 19:50
 */

@RestController
@RequestMapping("/admin/mall-manager")
@Api(value = "商城商户关系控制器",tags = "商城商户关系控制器")
public class MallManagerController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private MallManagerService mallManagerService;

    @Autowired
    private LogHelper logHelper;


    @RequiresPermissions("admin:mall-manager:select2")
    @RequiresPermissionsDesc(menu = {"商城商户关系管理", "商城商户关系管理"}, button = "查询下拉")
    @GetMapping("/select2")
    @ApiOperation(value = "获取商户列表", notes = "获取商户列表(排除管理员)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mallId", value = "商城id,获取商城关联商户", required = false, defaultValue = "", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "商户名称", required = false, defaultValue = "", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, defaultValue = "1", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页条数", required = false, defaultValue = "10",dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序字段", required = false, defaultValue = "add_time",dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序类型", required = false, defaultValue = "desc",dataType = "string", paramType = "query")
    })
    public Object select2(String mallId,
                          String name,
                          @RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer limit,
                          @Sort @RequestParam(defaultValue = "add_time") String sort,
                          @Order @RequestParam(defaultValue = "desc") String order) {
        //查询所有商户(排除管理员)
        List<Manager> adminList = adminService.queryMerchantSelective(name, page, limit, sort, order);
        List<Select2> select2List=new ArrayList<>();
        if(adminList.size()>0){
            if (mallId != null &&"".equals(mallId)) {
                //获取商城关联的商户
                List<MallManager> list = mallManagerService.querySelectiveByMallId(mallId);
                if(list.size()>0){
                    List<Integer> managerIdList=list.stream().map(MallManager::getManagerId).collect(Collectors.toList());
                    adminList=adminList.stream().filter(x->managerIdList.contains(x.getId())).collect(Collectors.toList());
                }
            }

            select2List=adminList.stream().map(x->{
                Select2 select2=new Select2();
                select2.setItemValue(x.getId().toString());
                select2.setItemText(x.getNickName());
                return select2;
            }).collect(Collectors.toList());
        }
        return ResponseUtil.okList(select2List);
    }


     @RequiresPermissions("admin:mall-manager:updateByMallId")
     @RequiresPermissionsDesc(menu = {"商城商户关系管理", "商城商户关系管理"}, button = "修改")
     @PostMapping("/updateByMallId")
     @ApiOperation(value = "更改商城与商户关系", notes = "更改商城与商户关系")
     @ApiImplicitParams({
             @ApiImplicitParam(name = "mallId", value = "商城id", required = true, defaultValue = "", dataType = "string", paramType = "query"),
             @ApiImplicitParam(name = "managerIds", value = "商户id(数组)", required = true, defaultValue = "", dataType = "integer",allowMultiple=true, paramType = "query"),
        })
     @Transactional(propagation = Propagation.REQUIRED)
     public Object updateByMallId (String mallId, @RequestBody Integer[] managerIds){
         //获取当前登陆用户信息
         Subject currentUser = SecurityUtils.getSubject();
         Manager currentAdmin= (Manager) currentUser.getPrincipal();
        //删除此商城与商户的所有关系
         mallManagerService.deleteByMallId(mallId);
         //建立商城与商户关系
         List<MallManager> list=new ArrayList<>();
         for (Integer managerId:managerIds) {
             MallManager mallManager=new MallManager();
             String uuid = IdHelper.generate32UUID();
             mallManager.setId(uuid);
             mallManager.setMallId(mallId);
             mallManager.setManagerId(managerId);
             list.add(mallManager);
         }
         mallManagerService.saveBatch(list);
         logHelper.logAuthSucceed("更改商城与商户关系", currentAdmin.getUsername());

         return ResponseUtil.ok();
    }
    @RequiresPermissions("admin:mall-manager:deleteByMallId")
    @RequiresPermissionsDesc(menu = {"商城商户关系管理", "商城商户关系管理"}, button = "删除")
    @PostMapping("/deleteByMallId")
    @ApiOperation(value = "删除商城与商户关系", notes = "删除商城与商户关系")
    @ApiImplicitParam(name = "mallId", value = "商城id", required = true, defaultValue = "", dataType = "string", paramType = "query")
    @Transactional(propagation = Propagation.REQUIRED)
    public Object deleteByMallId (String mallId){
        //获取当前登陆用户信息
        Subject currentUser = SecurityUtils.getSubject();
        Manager currentAdmin= (Manager) currentUser.getPrincipal();
        if (StringUtils.isEmpty(mallId)) {
            return ResponseUtil.badArgument();
        }
        //删除此商城与商户的所有关系
        mallManagerService.deleteByMallId(mallId);
        logHelper.logAuthSucceed("删除此商城与商户的所有关系", currentAdmin.getUsername());

        return ResponseUtil.ok();
    }

    //根据商城id，查询关联的商户信息列表
    @RequiresPermissions("admin:mall-manager:listByMallId")
    @RequiresPermissionsDesc(menu = {"商城商户关系管理", "商城商户关系管理"}, button = "查询")
    @GetMapping("/listByMallId")
    @ApiOperation(value = "查询关联的商户信息列表", notes = "查询关联的商户信息列表")
    @ApiImplicitParam(name = "mallId", value = "商城id", required = true, defaultValue = "", dataType = "string", paramType = "query")
    public Object listByMallId (String mallId){
        if (StringUtils.isEmpty(mallId)) {
            return ResponseUtil.badArgument();
        }
        //根据mallId获取关联的商户id集合
        List<MallManager> list = mallManagerService.querySelectiveByMallId(mallId);
        List<Integer> collect=new ArrayList<>();
        if(list.size()>0){
            //去重
            collect= list.stream().map(MallManager::getManagerId).distinct().collect(Collectors.toList());
        }
        List<Manager> managers = adminService.queryByIds(collect);
        return ResponseUtil.okList(managers);
    }
}
