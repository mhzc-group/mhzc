package com.beauty.mhzc.admin.web;

import com.beauty.mhzc.admin.annotation.RequiresPermissionsDesc;
import com.beauty.mhzc.admin.service.LogHelper;
import com.beauty.mhzc.admin.vo.MallVO;
import com.beauty.mhzc.core.validator.Order;
import com.beauty.mhzc.core.validator.Sort;
import com.beauty.mhzc.db.enums.ConstantEnums;
import com.beauty.mhzc.core.util.ResponseUtil;
import com.beauty.mhzc.db.domain.Mall;
import com.beauty.mhzc.db.domain.MallManager;
import com.beauty.mhzc.db.domain.Manager;
import com.beauty.mhzc.db.service.AdminService;
import com.beauty.mhzc.db.service.MallManagerService;
import com.beauty.mhzc.db.service.MallService;
import com.beauty.mhzc.db.service.impl.AdminMallServiceImpl;
import com.beauty.mhzc.db.service.impl.MallServiceImpl;
import com.beauty.mhzc.db.util.IdHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.beauty.mhzc.admin.util.AdminResponseCode.*;
/**
 * 商城控制器
 * @author xuan
 * @date 2020/10/20 19:49
 */
@RestController
@RequestMapping("/admin/mall")
@Api(value = "商城控制器",tags = "商城控制器")
public class MallController {

    private  MallService mallService;

    private  Manager currentAdmin;
    @Autowired
    private MallManagerService mallManagerService;
    @Autowired
    private MallServiceImpl mallServiceImpl;
    @Autowired
    private AdminMallServiceImpl adminMallServiceImpl;
    @Autowired
    private LogHelper logHelper;
    @Autowired
    private AdminService adminService;

    private void init(){
        //获取当前登陆用户信息
        Subject currentUser = SecurityUtils.getSubject();
        currentAdmin= (Manager) currentUser.getPrincipal();
        String type = currentAdmin.getType();
        if(type!=null&&type.equals(ConstantEnums.ADMINISTRATOR.getCode().toString())){
            mallService = adminMallServiceImpl;
        }else{
            mallService = mallServiceImpl;
        }
    }

    /**
     * 商城添加
     * @param mall
     * @param managerIds
     * @return
     */
    @RequiresPermissions("admin:mall:create")
    @RequiresPermissionsDesc(menu = {"商城管理", "商城管理"}, button = "添加")
    @PostMapping("/create")
    @ApiOperation(value = "商城添加", notes = "商城添加")
    @Transactional(propagation = Propagation.REQUIRED)
    //TODO 使用validate与bindingResult 进行参数验证
    public Object save(Mall mall,@RequestParam(value = "managerIds[]")Integer[] managerIds){
//        init();
        Subject currentUser = SecurityUtils.getSubject();
        currentAdmin= (Manager) currentUser.getPrincipal();
        //添加商城
        mall.setCreateBy(currentAdmin.getUsername());
        String id = adminMallServiceImpl.save(mall);
        //关联商城与商户
        // managerIds非空判断
        if(managerIds==null){
            return ResponseUtil.fail(ADMIN_INVALID_NAME,"商户不能为空");
        }
        List<MallManager> list=new ArrayList<>();
        for (Integer managerId:managerIds) {
            MallManager mallManager=new MallManager();
            String uuid = IdHelper.generate32UUID();
            mallManager.setId(uuid);
            mallManager.setMallId(id);
            //TODO mallManager表增加外键约束
            mallManager.setManagerId(managerId);
            list.add(mallManager);
        }
        mallManagerService.saveBatch(list);
        return ResponseUtil.ok();
    }

    /**
     * 商城编辑
     * @param mall
     * @return
     */
    @RequiresPermissions("admin:mall:update")
    @RequiresPermissionsDesc(menu = {"商城管理", "商城管理"}, button = "修改")
    @PostMapping("/update")
    @ApiOperation(value = "商城修改", notes = "商城修改")
    //TODO 使用validate与bindingResult 进行参数验证
    public Object update(Mall mall){
        Subject currentUser = SecurityUtils.getSubject();
        currentAdmin= (Manager) currentUser.getPrincipal();
        mall.setUpdateBy(currentAdmin.getUsername());
//        init();
        String anotherAdminId = mall.getId();
        if (anotherAdminId == null) {
            return ResponseUtil.badArgument();
        }
//        if (mallService.modifyById(mall) == 0) {
//            return ResponseUtil.updatedDataFailed();
//        }
        mall.setCreateBy(null);
        mall.setCreateOn(null);
        if (adminMallServiceImpl.modifyById(mall) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        logHelper.logAuthSucceed("编辑商城", currentAdmin.getUsername());
        return ResponseUtil.ok();
    }

    /**
     * 商城删除
     * @param id
     * @return
     */
    @RequiresPermissions("admin:mall:delete")
    @RequiresPermissionsDesc(menu = {"商城管理", "商城管理"}, button = "删除")
    @PostMapping("/delete")
    @ApiOperation(value = "商城删除", notes = "商城删除")
    public Object delete(String id){
//        init();
        if (id == null||"".equals(id.trim())) {
            return ResponseUtil.badArgument();
        }
        //逻辑删除
        adminMallServiceImpl.logicalDeleteById(id);
        logHelper.logAuthSucceed("删除商城", currentAdmin.getUsername());
        return ResponseUtil.ok();
    }


    @RequiresPermissions("admin:mall:list")
    @RequiresPermissionsDesc(menu = {"商城管理", "商城管理"}, button = "查询")
    @GetMapping("/list")
    @ApiOperation(value = "商城列表查询", notes = "商城列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "商城名称", required = false, defaultValue = "", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, defaultValue = "1", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页条数", required = false, defaultValue = "10",dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序字段", required = false, defaultValue = "create_on",dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序类型", required = false, defaultValue = "desc",dataType = "string", paramType = "query")
    })
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "create_on") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order){
        List<MallVO> mallVOList=new ArrayList<>();
        init();
        Integer managerId=currentAdmin.getId();
        //获取登陆人员关联的商城
        List<MallManager> list = mallManagerService.querySelectiveByManagerId(managerId);
        List<String > mallIds=new ArrayList<>();
        if(list.size()>0){
            mallIds=list.stream().map(MallManager::getMallId).collect(Collectors.toList());
        }
        List<Mall> malls = mallService.querySelective(name, page, limit, sort, order, mallIds);
        //获取全部商户集合
        List<Manager> managers = adminService.all();
        //获取全部商城商户关联关系集合
        List<MallManager> mallManagers = mallManagerService.queryAll();
        for (Mall mall:malls){
            MallVO mallVO=new MallVO();
            BeanUtils.copyProperties(mall,mallVO);
            String mallId = mall.getId();
            List<Integer> collect = mallManagers.stream()
                    .filter(x -> x.getMallId().equals(mallId))
                    .map(MallManager::getManagerId)
                    .collect(Collectors.toList());
            if(collect.size()>0){
                List<String> collect1 = managers.stream()
                        .filter(x -> collect.contains(x.getId()))
                        .map(Manager::getNickName)
                        .collect(Collectors.toList());
                mallVO.setManagerNames(collect1);
            }
            mallVOList.add(mallVO);
        }


        return ResponseUtil.okList(mallVOList);
    }

    /**
     *     根据商城id读取商城信息列表
     */
    @RequiresPermissions("admin:mall:read")
    @RequiresPermissionsDesc(menu = {"商城管理", "商城管理"}, button = "详情")
    @PostMapping("/read")
    @ApiOperation(value = "查看商城详情", notes = "查看商城详情")
    public Object read(String mallId){
        if (StringUtils.isEmpty(mallId)) {
            return ResponseUtil.badArgument();
        }
        init();
        Integer managerId=currentAdmin.getId();
        Mall mall = adminMallServiceImpl.queryById(mallId, managerId);
        return ResponseUtil.ok(mall);
    }






}
