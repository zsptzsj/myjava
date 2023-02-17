package com.qinge.springboot.service;

import com.qinge.springboot.common.Result;
import com.qinge.springboot.controller.dto.UserDto;
import com.qinge.springboot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 郑仕竣
 * @since 2023-02-06
 */
public interface IUserService extends IService<User> {

    UserDto login(UserDto userDto);

    User register(UserDto userDto);
}
