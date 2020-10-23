package com.beauty.mhzc.db.service;

import com.beauty.mhzc.db.dao.ManagerMapper;
import com.beauty.mhzc.db.domain.Manager;
import com.beauty.mhzc.db.domain.ManagerExample;
import com.beauty.mhzc.db.enums.ConstantEnums;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {
    private final Manager.Column[] result = new Manager.Column[]{Manager.Column.id, Manager.Column.username, Manager.Column.avatar, Manager.Column.roleIds};
    @Resource
    private ManagerMapper adminMapper;

    public List<Manager> findAdmin(String username) {
        ManagerExample example = new ManagerExample();
        example.or().andUsernameEqualTo(username).andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }

    public Manager findAdmin(Integer id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    public List<Manager> querySelective(String username, Integer page, Integer limit, String sort, String order) {
        ManagerExample example = new ManagerExample();
        ManagerExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andUsernameLike("%" + username + "%");
        }
        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return adminMapper.selectByExampleSelective(example, result);
    }


    public List<Manager> queryMerchantSelective(String username, Integer page, Integer limit, String sort, String order) {
        ManagerExample example = new ManagerExample();
        ManagerExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(username)) {
            criteria.andNickNameLike("%" + username + "%");
        }
        criteria.andDeletedEqualTo(false);
        criteria.andTypeEqualTo(ConstantEnums.MERCHANT.getCode().toString());

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return adminMapper.selectByExampleSelective(example);
    }


    public int updateById(Manager admin) {
        admin.setUpdateTime(LocalDateTime.now());
        return adminMapper.updateByPrimaryKeySelective(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(Manager admin) {
        admin.setAddTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.insertSelective(admin);
    }

    public Manager findById(Integer id) {
        return adminMapper.selectByPrimaryKeySelective(id, result);
    }

    public List<Manager> all() {
        ManagerExample example = new ManagerExample();
        example.or().andDeletedEqualTo(false);
        return adminMapper.selectByExample(example);
    }

    /**
     * 条件查询集合
     * @param ids
     * @return
     */
    public List<Manager> queryByIds(List<Integer> ids) {
        ManagerExample example = new ManagerExample();
        ManagerExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        return adminMapper.selectByExample(example);
    }
}
