package com.qinge.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qinge.springboot.entity.Menu;
import com.qinge.springboot.entity.Role;
import com.qinge.springboot.entity.RoleMenu;
import com.qinge.springboot.entity.User;
import com.qinge.springboot.mapper.RoleMapper;
import com.qinge.springboot.mapper.RoleMenuMapper;
import com.qinge.springboot.service.IMenuService;
import com.qinge.springboot.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 郑仕竣
 * @since 2023-02-09
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService iMenuService;

    @Transactional
    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
//        QueryWrapper<RoleMenu> querWrapper = new QueryWrapper<>();
//        querWrapper.eq("role_id",roleId);
//        roleMenuMapper.delete(querWrapper);
        //先删除当前角色id所有的绑定关系
        roleMenuMapper.deleteByRoleId(roleId);

        List<Integer> menuIdsCopy = CollUtil.newArrayList(menuIds);
        for (Integer menuId : menuIds){
            Menu menu = iMenuService.getById(menuId);
            if(menu.getPid() !=null &&!menuIdsCopy.contains(menu.getPid())){
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menu.getPid());
                roleMenuMapper.insert(roleMenu);
                menuIdsCopy.add((menu.getPid()));
            }
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<Integer> getRoleMenu(Integer roleId) {
        return roleMenuMapper.selectByRoleId(roleId);
    }
}
