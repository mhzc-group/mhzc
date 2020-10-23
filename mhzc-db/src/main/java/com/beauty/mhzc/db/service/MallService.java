package com.beauty.mhzc.db.service;

import com.beauty.mhzc.db.domain.Mall;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xuan
 * @date 2020/10/20 20:39
 */

public interface MallService {
    String save(Mall mall);

    Integer modifyById(Mall mall);

    void logicalDeleteById(String id);

    List<Mall> querySelective(String name, Integer page, Integer limit, String sort, String order, List<String> mallIds);

    Mall queryById(String mallId, Integer managerId);
}