package com.beauty.mhzc.db.service;

import com.alibaba.druid.util.StringUtils;
import com.beauty.mhzc.db.dao.MaterialMapper;
import com.beauty.mhzc.db.domain.Material;
import com.beauty.mhzc.db.domain.MaterialExample;
import com.beauty.mhzc.db.util.IdHelper;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuan
 * @date 2020/11/2 20:59
 */

@Service
public class MaterialService {
    @Resource
    private MaterialMapper materialMapper;

    public String save(Material material){
        String id= IdHelper.generate32UUID();
        material.setId(id);
        materialMapper.insertSelective(material);
        return id;
    }

    public Integer modifyById(Material material){
        return  materialMapper.updateByPrimaryKeySelective(material);
    }

    public Material queryById(String id){
        return materialMapper.selectByPrimaryKey(id);
    }

    public List<Material> queryList(String appId,Integer page, Integer limit, String sort, String order){
        MaterialExample example = new MaterialExample();
        MaterialExample.Criteria criteria=example.createCriteria();
        example.or().andAppIdEqualTo(appId);
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        List<Material> materials = materialMapper.selectByExample(example);
        return materials;
    }

    public void deleteById(String id){
        materialMapper.deleteByPrimaryKey(id);
    }

    public void logicalDeleteById(String id){
        materialMapper.logicalDeleteByPrimaryKey(id);
    }
}
