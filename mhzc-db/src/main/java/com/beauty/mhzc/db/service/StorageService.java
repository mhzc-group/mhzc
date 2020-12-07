package com.beauty.mhzc.db.service;

import com.beauty.mhzc.db.dao.StorageMapper;
import com.beauty.mhzc.db.domain.Storage;
import com.beauty.mhzc.db.domain.StorageExample;
import com.beauty.mhzc.db.util.IdHelper;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xuan
 * @date 2020/10/28 20:57
 */

@Service
public class StorageService {
    @Resource
    private StorageMapper storageMapper;

    public void deleteByKey(String key) {
        StorageExample example = new StorageExample();
        example.or().andKeyEqualTo(key);
        storageMapper.deleteByExample(example);
    }

    public void deleteLogicalByKey(String key) {
        StorageExample example = new StorageExample();
        example.or().andKeyEqualTo(key);
        storageMapper.logicalDeleteByExample(example);
    }

    public String add(Storage storageInfo) {
        String id = IdHelper.generate32UUID();
        storageInfo.setId(id);
        storageInfo.setCreateOn(LocalDateTime.now());
        storageInfo.setUpdateOn(LocalDateTime.now());
        storageMapper.insertSelective(storageInfo);
        return id;
    }

    public Storage findByKey(String key) {
        StorageExample example = new StorageExample();
        example.or().andKeyEqualTo(key).andDeletedEqualTo(false);
        return storageMapper.selectOneByExample(example);
    }

    public int update(Storage storageInfo) {
        storageInfo.setUpdateOn(LocalDateTime.now());
        return storageMapper.updateByPrimaryKeySelective(storageInfo);
    }

    public Storage findById(String id) {
        return storageMapper.selectByPrimaryKey(id);
    }

    /**
     * 获取文件集合
     * @param ids
     * @return
     */
    public List<Storage> queryList(List<String> ids) {
        StorageExample example = new StorageExample();
        StorageExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        List<Storage> storageList = storageMapper.selectByExample(example);
        return storageList;
    }

    public List<Storage> querySelective(String key, String name, Integer page, Integer limit, String sort, String order,String appId) {
        StorageExample example = new StorageExample();
        StorageExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(key)) {
            criteria.andKeyEqualTo(key);
        }
        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);
        criteria.andAppIdEqualTo(appId);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return storageMapper.selectByExample(example);
    }
}
