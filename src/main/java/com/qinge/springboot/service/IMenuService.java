package com.qinge.springboot.service;

import com.qinge.springboot.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 郑仕竣
 * @since 2023-02-09
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
