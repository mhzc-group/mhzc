package com.beauty.mhzc.db.service;

import com.beauty.mhzc.db.dao.MallManagerMapper;
import com.beauty.mhzc.db.domain.MallManager;
import com.beauty.mhzc.db.domain.MallManagerExample;
import com.beauty.mhzc.db.util.IdHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商城管理员关系服务
 * @author xuan
 * @date 2020/10/20 19:51
 */

@Service
public class MallManagerService {
    @Resource
    private MallManagerMapper mallManagerMapper;

    public void saveBatch(List<MallManager> list){
        mallManagerMapper.insertBatch(list);
    }

    /**
     * 条件查询列表
     * @param managerId
     * @return
     */
    public List<MallManager> querySelectiveByManagerId(Integer managerId){
        MallManagerExample example=new MallManagerExample();
        MallManagerExample.Criteria criteria = example.createCriteria();
        criteria.andManagerIdEqualTo(managerId);
        return mallManagerMapper.selectByExample(example);
    }

    /**
     * 条件查询列表
     * @param mallId
     * @return
     */
    public List<MallManager> querySelectiveByMallId(String mallId){
        MallManagerExample example=new MallManagerExample();
        MallManagerExample.Criteria criteria = example.createCriteria();
        criteria.andMallIdEqualTo(mallId);
        return mallManagerMapper.selectByExample(example);
    }

    /**
     * 删除商城与商户关联关系
     * @return
     */
    public Integer deleteByMallId(String mallId){
        MallManagerExample example=new MallManagerExample();
        MallManagerExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(mallId)) {
            criteria.andMallIdEqualTo(mallId);
        }
        return mallManagerMapper.deleteByExample(example);
    }

    /**
     * 查询全部关联关系
     * @return
     */
    public List<MallManager> queryAll(){
        MallManagerExample example=new MallManagerExample();
        return mallManagerMapper.selectByExample(example);
    }
}
