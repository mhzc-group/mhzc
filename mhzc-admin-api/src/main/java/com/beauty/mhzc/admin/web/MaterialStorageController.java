package com.beauty.mhzc.admin.web;

import com.beauty.mhzc.admin.annotation.RequiresPermissionsDesc;
import com.beauty.mhzc.core.storage.GenericStorageService;
import com.beauty.mhzc.core.util.ResponseUtil;
import com.beauty.mhzc.core.validator.Order;
import com.beauty.mhzc.core.validator.Sort;
import com.beauty.mhzc.db.domain.MaterialStorage;
import com.beauty.mhzc.db.domain.Storage;
import com.beauty.mhzc.db.service.MaterialStorageService;
import com.beauty.mhzc.db.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

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


    @Autowired
    private GenericStorageService genericStorageService;


    @RequiresPermissions("admin:material_storage:list")
    @RequiresPermissionsDesc(menu = {"素材文件管理控制器", "素材文件管理控制器"}, button = "查询列表")
    @GetMapping("/list")
    @ApiOperation(value = "根据素材分组获取文件集合", notes = "查询素材文件列表,根据 素材分组id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "素材分组id", required = true, defaultValue = "", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, defaultValue = "1", dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "limit", value = "每页条数", required = false, defaultValue = "10",dataType = "integer", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序字段", required = false, defaultValue = "create_on",dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "order", value = "排序类型", required = false, defaultValue = "desc",dataType = "string", paramType = "query")
    })
    public Object list(@NotEmpty String id,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "create_on") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order){
        //根据素材分组获取文件集合
        List<Storage> storageList = service.queryPageByMaterialId(id, page, limit, sort, order);
        return ResponseUtil.okList(storageList);
    }
    
    @RequiresPermissions("admin:material_storage:create")
    @RequiresPermissionsDesc(menu = {"素材文件管理控制器", "素材文件管理控制器"}, button = "添加")
    @GetMapping("/create")
    @ApiOperation(value = "根据分组id，添加上传文件", notes = "根据分组id，添加上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "素材分组id", required = true, defaultValue = "", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "appId", value = "appId", required = true, defaultValue = "", dataType = "string", paramType = "query")
    })
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public Object create(@NotEmpty String id, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        //获取appId
        String appId = (String) session.getAttribute("appId");
        String originalFilename = file.getOriginalFilename();
        Storage storage = genericStorageService.store(file.getInputStream(), file.getSize(),
                file.getContentType(), originalFilename,appId);

        MaterialStorage materialStorage=new MaterialStorage();
        materialStorage.setMaterialId(id);
        materialStorage.setStorageId(storage.getId());
        service.save(materialStorage);
        return ResponseUtil.ok(storage);
    }
}
