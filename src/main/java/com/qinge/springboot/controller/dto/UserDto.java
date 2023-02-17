package com.qinge.springboot.controller.dto;

import com.qinge.springboot.entity.Menu;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token;
    private List<Menu> menus;
    private String role;
}
