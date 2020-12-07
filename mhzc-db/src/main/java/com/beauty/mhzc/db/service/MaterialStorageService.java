package com.beauty.mhzc.db.service;

import com.alibaba.druid.util.StringUtils;
import com.beauty.mhzc.db.dao.MaterialStorageMapper;
import com.beauty.mhzc.db.dao.StorageMapper;
import com.beauty.mhzc.db.domain.MaterialStorage;
import com.beauty.mhzc.db.domain.MaterialStorageExample;
import com.beauty.mhzc.db.domain.Storage;
import com.beauty.mhzc.db.domain.StorageExample;
import com.beauty.mhzc.db.util.IdHelper;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xuan
 * @date 2020/12/6 22:29
 */

@Service
public class MaterialStorageService {

    @Resource
    private MaterialStorageMapper mapper;

    @Resource
    private StorageMapper storageMapper;

    /**
     * 根据素材分组获取文件集合id
     *
     * @param id 素材分组id
     * @return
     */
    public List<String> queryByMaterialId(String id){
        MaterialStorageExample example=new MaterialStorageExample();
        MaterialStorageExample.Criteria criteria = example.createCriteria();
        criteria.andMaterialIdEqualTo(id);
        List<MaterialStorage> materialStorages = mapper.selectByExample(example);
        List<String> collect = materialStorages.stream().map(MaterialStorage::getStorageId).collect(Collectors.toList());
        return collect;
    }

    /**
     * 分页
     * 根据素材分组获取文件集合id
     *
     * @param id 素材分组id
     * @return
     */
    public List<Storage> queryPageByMaterialId(String id,Integer page, Integer limit, String sort, String order){
        MaterialStorageExample example=new MaterialStorageExample();
        MaterialStorageExample.Criteria criteria = example.createCriteria();
        criteria.andMaterialIdEqualTo(id);

        List<MaterialStorage> materialStorages = mapper.selectByExample(example);
        List<String> collect = materialStorages.stream().map(MaterialStorage::getStorageId).collect(Collectors.toList());

        StorageExample example1 = new StorageExample();
        StorageExample.Criteria criteria1 = example1.createCriteria();
        criteria1.andIdIn(collect);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }
        PageHelper.startPage(page, limit);
        List<Storage> storageList = storageMapper.selectByExample(example1);
        return storageList;
    }

    public String  save(MaterialStorage materialStorage){
        String id= IdHelper.generate32UUID();
        materialStorage.setId(id);
        mapper.insert(materialStorage);
        return id;
    }
}
