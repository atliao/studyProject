package com.la.xuecheng.ucenter.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.la.xuecheng.ucenter.mapper.XcMenuMapper;
import com.la.xuecheng.ucenter.mapper.XcUserMapper;
import com.la.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.la.xuecheng.ucenter.model.dto.XcUserExt;
import com.la.xuecheng.ucenter.model.po.XcMenu;
import com.la.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/24 10:37
 */
@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {

    @Resource
    XcUserMapper xcUserMapper;

    @Resource
    XcMenuMapper xcMenuMapper;

    @Autowired
    ApplicationContext applicationContext;



    //传入的请求认证的参数就是AuthParamsDto
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //将传入的json转成AuthParamsDto对象
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            throw new RuntimeException("请求认证参数不符合要求");
        }

        //认证类型，有password，wx。。。
        String authType = authParamsDto.getAuthType();


        //根据认证类型从spring容器取出指定的bean
        String beanName = authType+"_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        //调用统一execute方法完成认证
        XcUserExt xcUserExt = authService.execute(authParamsDto);
        //封装xcUserExt用户信息为UserDetails

        UserDetails userPrincipal = getUserPrincipal(xcUserExt);

        return userPrincipal;
    }

    /**
     * @description 查询用户信息
     * @param xcUser  用户id，主键
     * @return com.xuecheng.ucenter.model.po.XcUser 用户信息
     * @author Mr.M
     * @date 2022/9/29 12:19
     */
    public UserDetails getUserPrincipal(XcUserExt xcUser){
        String password = xcUser.getPassword();
        //根据用户id查权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(xcUser.getId());
        List<String> permission = new ArrayList<>();
        if(xcMenus.size() > 0){
            for(XcMenu xcMenu : xcMenus){
                permission.add(xcMenu.getCode());
            }
        }
        //权限
        String[] authorities= permission.toArray(new String[0]);
        xcUser.setPassword(null);
        //将用户信息转json
        String userJson = JSON.toJSONString(xcUser);
        UserDetails userDetails = User.withUsername(userJson).password(password).authorities(authorities).build();
        return  userDetails;
    }


}
