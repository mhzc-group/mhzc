package com.beauty.mhzc.admin.web;

import com.beauty.mhzc.admin.annotation.RequiresPermissionsDesc;
import com.beauty.mhzc.core.storage.GenericStorageService;
import com.beauty.mhzc.core.util.ResponseUtil;
import com.beauty.mhzc.core.validator.Order;
import com.beauty.mhzc.core.validator.Sort;
import com.beauty.mhzc.db.domain.Storage;
import com.beauty.mhzc.db.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/storage")
@Api(value = "文件存储管理控制器",tags = "文件存储管理控制器")
@Validated
public class StorageController {
    private final Log logger = LogFactory.getLog(StorageController.class);

    @Autowired
    private GenericStorageService genericStorageService;
    @Autowired
    private StorageService storageService;

    @RequiresPermissions("admin:storage:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "对象存储"}, button = "查询")
    @GetMapping("/list")
    public Object list(String key, String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "create_on") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order,String appId) {
        List<Storage> storageList = storageService.querySelective(key, name, page, limit, sort, order,appId);
        return ResponseUtil.okList(storageList);
    }

    @RequiresPermissions("admin:storage:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "对象存储"}, button = "上传")
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @PostMapping("/create")
    public Object create(@RequestParam("file") MultipartFile file,@NotEmpty String  appId) throws IOException {

        String originalFilename = file.getOriginalFilename();
        Storage storage = genericStorageService.store(file.getInputStream(), file.getSize(),
                file.getContentType(), originalFilename,appId);
        return ResponseUtil.ok(storage);
    }

    @RequiresPermissions("admin:storage:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "对象存储"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotEmpty String  id) {
        Storage storageInfo = storageService.findById(id);
        if (storageInfo == null) {
            return ResponseUtil.badArgumentValue();
        }
        return ResponseUtil.ok(storageInfo);
    }

    @RequiresPermissions("admin:storage:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "对象存储"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody Storage storage) {
        if (storageService.update(storage) == 0) {
            return ResponseUtil.updatedDataFailed();
        }
        return ResponseUtil.ok(storage);
    }

    @RequiresPermissions("admin:storage:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "对象存储"}, button = "删除")
    @GetMapping("/delete")
    public Object delete(@NotEmpty String  key) {
        if (StringUtils.isEmpty(key)) {
            return ResponseUtil.badArgument();
        }
        genericStorageService.delete(key);
        return ResponseUtil.ok();
    }
}
