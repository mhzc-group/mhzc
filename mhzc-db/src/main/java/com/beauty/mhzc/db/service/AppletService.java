package com.beauty.mhzc.db.service;

import com.beauty.mhzc.db.dao.AppletMapper;
import com.beauty.mhzc.db.domain.Applet;
import com.beauty.mhzc.db.domain.AppletExample;
import com.beauty.mhzc.db.util.IdHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuan
 * @date 2020/11/1 15:56
 */

@Service
public class AppletService {
    @Resource
    private AppletMapper appletMapper;

    public String save(Applet applet){
        String id= IdHelper.generate32UUID();
        applet.setId(id);
        appletMapper.insertSelective(applet);
        return id;
    }

    public Integer modifyById(Applet applet){
        return  appletMapper.updateByPrimaryKeySelective(applet);
    }

    public Applet queryById(String id){
        return appletMapper.selectByPrimaryKey(id);
    }

    public List<Applet> queryByList(){
        AppletExample appletExample=new AppletExample();
        appletExample.or().andDeletedEqualTo(false);
        return appletMapper.selectByExample(appletExample);
    }
}
