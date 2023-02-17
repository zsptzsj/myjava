package com.qinge.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qinge.springboot.common.Constants;
import com.qinge.springboot.controller.dto.UserDto;
import com.qinge.springboot.entity.Menu;
import com.qinge.springboot.entity.User;
import com.qinge.springboot.exception.ServiceException;
import com.qinge.springboot.mapper.RoleMapper;
import com.qinge.springboot.mapper.RoleMenuMapper;
import com.qinge.springboot.mapper.UserMapper;
import com.qinge.springboot.service.IMenuService;
import com.qinge.springboot.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qinge.springboot.utils.TokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 郑仕竣
 * @since 2023-02-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log LOG = Log.get();

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService iMenuService;

    @Override
    public UserDto login(UserDto userDto) {
        User one=getUserInfo(userDto);
        if(one !=null){
            BeanUtil.copyProperties(one,userDto,true);
            String token = TokenUtils.genToken(one.getId().toString(),one.getPassword());
            userDto.setToken(token);

            String role = one.getRole();

            List<Menu> roleMenus = getRoleMenus(role);
            userDto.setMenus(roleMenus);
            return userDto;
        }else {
            throw new ServiceException(Constants.CODE_600,"用户名或密码错误");
        }

    }

    @Override
    public User register(UserDto userDto) {
        User one=getUserInfo(userDto);
        if(one == null){
            one = new User();
            BeanUtil.copyProperties(userDto,one,true);
            save(one);
        }else {
            throw new ServiceException(Constants.CODE_600,"用户以存在");
        }
        return one;
    }
    private User getUserInfo(UserDto userDto){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",userDto.getUsername());
        queryWrapper.eq("password",userDto.getPassword());
        User one;
        try{
            one = getOne(queryWrapper);
        }catch (Exception e){
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500,"系统错误");
        }
        return one;
    }
    private List<Menu> getRoleMenus(String roleFlag){
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        List<Integer> menuIds =  roleMenuMapper.selectByRoleId(roleId);
        List<Menu> menus = iMenuService.findMenus("");
        List<Menu> roleMenus = new ArrayList<>();
        for (Menu menu : menus) {
            if(menuIds.contains(menu.getId())){
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }
}
