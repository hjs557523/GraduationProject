package com.hjs.system.config.shiro.common;

import com.hjs.system.config.shiro.FreeLogin.FreeRealm;
import com.hjs.system.config.shiro.student.StudentRealm;
import com.hjs.system.config.shiro.teacher.TeacherRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 黄继升 16041321
 * @Description:
 * @date Created in 2020/3/6 15:04
 * @Modified By:
 */

@Configuration
public class ShiroConfig {

    private static final Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

    //获取application.yml参数
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.password}")
    private String password;


    /**
     * SecurityManager 是 Shiro 架构的核心，通过它来链接Realm和用户（文档中称之为Subject）
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        // 设置自定义的多realm认证器
        securityManager.setAuthenticator(modularRealmAuthenticator());
        List<Realm> realms = new ArrayList<>();

        /* 添加多个Realm: */

        // 添加学生Realm数据源
        realms.add(studentShiroRealm());

        // 添加教师Realm数据源
        realms.add(teacherShiroRealm());

        // 添加Github免密登录Realm数据源
        realms.add(githubShiroRealm());

        // 自定义缓存实现，使用redis实现spring-cache
        securityManager.setCacheManager(cacheManager());

        // 自定义session管理，使用redis来管理session
        securityManager.setSessionManager(sessionManager());

        // 注入记住我管理器
        securityManager.setRememberMeManager(rememberMeManager());

        // 设置Realms
        securityManager.setRealms(realms);
        // 设置securityManager 的realms一定要放到最后，因为在调用SecurityManager.setRealms时会将realms设置给authorizer，
        // 并为各个Realm设置permissionResolver和rolePermissionResolver

        return securityManager;
    }


    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        logger.info("拦截器配置链初始化...");

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //setLoginUrl 如果不设置值，默认会自动寻找Web工程根目录下的"/login.jsp"页面 或 "/login" 映射
//        shiroFilterFactoryBean.setLoginUrl("/login.html");

        //登录成功后跳转的url
        //shiroFilterFactoryBean.setSuccessUrl("/index");

        //登录失败(无权限)后跳转的url
        //shiroFilterFactoryBean.setUnauthorizedUrl("/index");


        /**
         * Shiro内置过滤器，可以实现权限相关的拦截器
         *    常用的过滤器：
         *       anon: 无需认证（登录）可以访问
         *       authc: 必须认证才可以访问
         *       user: 如果使用rememberMe的功能可以直接访问
         *       perm： 该资源必须得到资源权限才可以访问
         *       role: 该资源必须得到角色权限才可以访问
         */
//        // 设置过滤器
//        //（1）获取filters
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
//
//        //（2）将自定义的权限验证失败的过滤器ShiroFilterFactoryBean注入shiroFilter
        filters.put("authc", new ShiroLoginFilter());
//
        //（3）开始顺序配置访问权限（过滤链）
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();//项目难点：LinkedHashMap可以保证顺序

        filterChainDefinitionMap.put("/", "anon");//访问localhost的时候能够不会被拦截，自动跳转到login.html
        filterChainDefinitionMap.put("/login.html", "anon");
        filterChainDefinitionMap.put("/binding.html", "anon");
        filterChainDefinitionMap.put("/templates/**","anon");
        filterChainDefinitionMap.put("/websocket.html", "anon");
        filterChainDefinitionMap.put("/websocket/**", "anon");
        filterChainDefinitionMap.put("/callback", "anon");
        filterChainDefinitionMap.put("/wx/**", "anon");
        filterChainDefinitionMap.put("/web/**", "anon");
        filterChainDefinitionMap.put("/student/login","anon");
        filterChainDefinitionMap.put("/teacher/login","anon");
        filterChainDefinitionMap.put("/admin/login","anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/druid/**","anon");
//        filterChainDefinitionMap.put("/hello","anon");
//        filterChainDefinitionMap.put("/hello2","anon");
        filterChainDefinitionMap.put("/static/**","anon");
        filterChainDefinitionMap.put("/student/**","roles[Student]"); //对应之前那个addRoles(Student)
        filterChainDefinitionMap.put("/teacher/**","roles[Teacher]");




        // 过滤链定义，从上向下顺序执行，一般将/**放在最为下边
        filterChainDefinitionMap.put("/**","authc");


        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        logger.info("拦截器链: " + shiroFilterFactoryBean.getFilterChainDefinitionMap());
        logger.info("拦截器链初始化结束.");

        return shiroFilterFactoryBean;
    }




    /**
     * 配置shiro redisManager
     * 使用的是shiro-redis开源插件
     * @return
     */
    @Bean
    public RedisManager redisManager() {
        logger.info("创建shiro的redisManager，开始连接Redis...URL=" + host + ":" + port);
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setPassword(password);
        redisManager.setExpire(6 * 60 * 60);//配置缓存过期时间6小时
        redisManager.setTimeout(timeout);
        return redisManager;
    }


    /**
     * cacheManager的Redis实现
     * 使用的是shiro-redis开源插件
     * @return
     */
    public RedisCacheManager cacheManager() {
        logger.info("创建RedisCacheManager...");
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }


    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * 使用的是shiro-redis开源插件
     * @return
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }


    /**
     * Session Manager
     * 使用的是shiro-redis开源插件
     * @return
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();//默认sessionManager
        sessionManager.setSessionDAO(redisSessionDAO());
        // 去掉shiro重定向时时url里的JSESSIONID：shiro1.3.2版本及以上才能使用该方法
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }


    /**
     * cookie对象
     * @return
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        // 这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        // 记住我cookie生效时间30天，单位秒
        simpleCookie.setMaxAge(60 * 60 * 24 * 30);
        return simpleCookie;
    }


    /**
     * cookie管理对象：记住我功能
     * @return
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        return cookieRememberMeManager;
    }


    /**
     * 系统自带的Realm管理，主要针对多realm
     * @return
     */
    @Bean
    public ModularRealmAuthenticator modularRealmAuthenticator() {
        //自己重写的ModularRealmAuthenticator
        UserModularRealmAuthenticator modularRealmAuthenticator = new UserModularRealmAuthenticator();
        modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return modularRealmAuthenticator;
    }



    /**
     * 设置解密规则：
     * 因为我们的密码是加过密，所以，如果要Shiro验证用户身份的话，需要告诉它我们用的是md5加密的，并且是加密了两次。
     * 同时我们在自己的Realm中也通过SimpleAuthenticationInfo返回了加密时使用的盐。这样Shiro就能顺利的解密密码并验
     * 证用户名和密码是否正确了。
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5"); // 散列算法：这里使用MD5算法
        hashedCredentialsMatcher.setHashIterations(2); // 散列的次数，比如散列两次，相当于 md5(md5(""))
        return hashedCredentialsMatcher;
    }



    /**
     * 学生Realm
     * @return
     */
    @Bean
    public StudentRealm studentShiroRealm() {
        StudentRealm studentRealm = new StudentRealm();
        studentRealm.setCredentialsMatcher(hashedCredentialsMatcher());//设置解密规则
        return studentRealm;
    }


    /**
     * 教师Realm
     * @return
     */
    @Bean
    public TeacherRealm teacherShiroRealm() {
        TeacherRealm teacherRealm = new TeacherRealm();
        teacherRealm.setCredentialsMatcher(hashedCredentialsMatcher()); //设置解密规则
        return teacherRealm;

    }


    /**
     * 免密Realm —— Github用户
     * @return
     */
    @Bean
    public FreeRealm githubShiroRealm() {
        FreeRealm freeRealm = new FreeRealm();
        // 这里不设置定义解密规则, 就是默认用数据库密码来进行匹配校验
        return freeRealm;
    }



    /**
     * 开启shiro aop注解支持
     * 使用代理方式，所以需要开启代码支持
     * 开启 权限注解，Controller才能使用@RequiresPermissions
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
