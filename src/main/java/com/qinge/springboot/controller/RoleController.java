package com.qinge.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinge.springboot.common.Result;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

import com.qinge.springboot.service.IRoleService;
import com.qinge.springboot.entity.Role;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 郑仕竣
 * @since 2023-02-09
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    private IRoleService roleService;

    @PostMapping
    public Result save(@RequestBody Role role){
        roleService.saveOrUpdate(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        roleService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        roleService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(roleService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(roleService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Role> querWrapper = new QueryWrapper<>();
        querWrapper.like("name", name);
        querWrapper.orderByDesc("id");
        return Result.success(roleService.page(new Page<>(pageNum, pageSize),querWrapper));
    }

    @PostMapping("/roleMenu/{roleId}")
    public Result roleMenu(@PathVariable Integer roleId, @RequestBody List<Integer> menuIds){
        roleService.setRoleMenu(roleId,menuIds);
        return Result.success();
    }

    @GetMapping("/roleMenu/{roleId}")
    public Result getroleMenu(@PathVariable Integer roleId){
        return Result.success(roleService.getRoleMenu(roleId));
    }
}

