package com.qinge.springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qinge.springboot.entity.Menu;
import com.qinge.springboot.mapper.MenuMapper;
import com.qinge.springboot.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 郑仕竣
 * @since 2023-02-09
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Override
    public List<Menu> findMenus(String name) {
        QueryWrapper<Menu> querWrapper = new QueryWrapper<>();
        if(StrUtil.isNotBlank(name)){
            querWrapper.like("name",name);
        }

//        querWrapper.orderByDesc("id");
        List<Menu> list = list(querWrapper);
        //找到pid为null的一级菜单
        List<Menu> parentNodes = list.stream().filter(menu -> menu.getPid() == null).collect(Collectors.toList());
        for (Menu menu:parentNodes){
            menu.setChildren(list.stream().filter(m -> menu.getId().equals(m.getPid())).collect(Collectors.toList()));
        }
        return parentNodes;
    }
}
