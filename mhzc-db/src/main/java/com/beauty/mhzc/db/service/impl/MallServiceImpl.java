package com.beauty.mhzc.db.service.impl;

import com.beauty.mhzc.db.dao.MallMapper;
import com.beauty.mhzc.db.domain.Mall;

import com.beauty.mhzc.db.domain.MallExample;
import com.beauty.mhzc.db.service.MallService;
import com.beauty.mhzc.db.util.IdHelper;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 商城服务
 * @author xuan
 * @date 2020/10/20 19:51
 */

@Service
public class MallServiceImpl implements MallService {
    @Resource
    private MallMapper mallMapper;

    @Override
    public String save(Mall mall){
        String id= IdHelper.generate32UUID();
        mall.setId(id);
        mallMapper.insertSelective(mall);
        return id;
    }


    @Override
    public Integer modifyById(Mall mall){
        return mallMapper.updateByPrimaryKeySelective(mall);
    }

    @Override
    public void logicalDeleteById(String id) {
        mallMapper.logicalDeleteByPrimaryKey(id);
    }

    @Override
    public void deleteById(String id) {
        mallMapper.deleteByPrimaryKey(id);
    }
    /**
     * 按mallId
     * 分页查询商城列表
     * @param name
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @param mallIds
     * @return
     */
    @Override
    public List<Mall> querySelective(String name, Integer page, Integer limit, String sort, String order,List<String > mallIds) {
        List<Mall> mallList=new ArrayList<>();
        if(mallIds==null||mallIds.size()<=0){
            return mallList;
        }
        MallExample example = new MallExample();
        MallExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andMallNameLike("%" + name + "%");
        }
        criteria.andDeletedEqualTo(false);
        criteria.andIdIn(mallIds);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        mallList= mallMapper.selectByExampleSelective(example);
        return mallList;
    }

    @Override
    public Mall queryById(String mallId,Integer managerId) {
        Mall mall = mallMapper.selectByCondition(mallId, managerId);
        return mall;
    }
}
