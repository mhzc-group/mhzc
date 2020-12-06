package com.beauty.mhzc.db.service;

import com.beauty.mhzc.db.dao.MaterialStorageMapper;
import com.beauty.mhzc.db.domain.MaterialStorage;
import com.beauty.mhzc.db.domain.MaterialStorageExample;
import com.beauty.mhzc.db.domain.Storage;
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

    /**
     * 根据素材分组获取文件集合id
     *
     * @param id 素材分组id
     * @return
     */
    public List<String> queryByMaterial(String id){
        MaterialStorageExample example=new MaterialStorageExample();
        MaterialStorageExample.Criteria criteria = example.createCriteria();
        criteria.andMaterialIdEqualTo(id);
        List<MaterialStorage> materialStorages = mapper.selectByExample(example);
        List<String> collect = materialStorages.stream().map(MaterialStorage::getStorageId).collect(Collectors.toList());
        return collect;
    }
}
