package com.beauty.mhzc.wx.shiro;


import com.beauty.mhzc.wx.filter.JwtFilter;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;
/**
 * @author xuan
 * @date 2020/12/21 21:58
 */

@Configuration
public class ShirConfig {

    /**
     * SecurityManager,安全管理器,所有与安全相关的操作都会与之进行交互;
     * 它管理着所有Subject,所有Subject都绑定到SecurityManager,与Subject的所有交互都会委托给SecurityManager
     * DefaultWebSecurityManager :
     * 会创建默认的DefaultSubjectDAO(它又会默认创建DefaultSessionStorageEvaluator)
     * 会默认创建DefaultWebSubjectFactory
     * 会默认创建ModularRealmAuthenticator
     */
    @Bean
    public DefaultWebSecurityManager securityManager(CustomRealm customRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm
        securityManager.setRealms(customRealm.allRealm());
        DefaultSubjectDAO subjectDAO = (DefaultSubjectDAO) securityManager.getSubjectDAO();
        // 关闭自带session
        DefaultSessionStorageEvaluator evaluator = (DefaultSessionStorageEvaluator) subjectDAO.getSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(Boolean.FALSE);
        subjectDAO.setSessionStorageEvaluator(evaluator);
        return securityManager;
    }
    /**
     * 配置Shiro的访问策略
     */
    @Bean
    public ShiroFilterFactoryBean factory(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new JwtFilter());
//        filterMap.put("auth", new AuthFilter());
        factoryBean.setFilters(filterMap);
        factoryBean.setSecurityManager(securityManager);
        Map<String, String> filterRuleMap = new HashMap<>();
        //登陆相关api不需要被过滤器拦截
        filterRuleMap.put("/wx/user/*/login", "anon");

        //swagger接口权限 开放
        filterRuleMap.put("/swagger-ui/**", "anon");
        filterRuleMap.put("/v3/**", "anon");
        filterRuleMap.put("/doc/**", "anon");


        // 所有请求通过JWT Filter
        filterRuleMap.put("/**", "jwt");
//        filterRuleMap.put("/**", "jwt,auth");
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        return factoryBean;
    }
    /**
     * 添加注解支持
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true); // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        return defaultAdvisorAutoProxyCreator;
    }
    /**
     * 添加注解依赖
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启注解验证
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
