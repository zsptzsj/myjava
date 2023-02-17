package com.qinge.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinge.springboot.common.Constants;
import com.qinge.springboot.common.Result;
import com.qinge.springboot.entity.Dict;
import com.qinge.springboot.mapper.DictMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import com.qinge.springboot.service.IMenuService;
import com.qinge.springboot.entity.Menu;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 郑仕竣
 * @since 2023-02-09
 */
@RestController
@RequestMapping("/menu")
public class MenuController {
    @Resource
    private IMenuService menuService;
    @Resource
    private DictMapper dictMapper;

    @PostMapping
    public Result save(@RequestBody Menu menu){
        menuService.saveOrUpdate(menu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        menuService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        menuService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping("/ids")
    public Result findAllIds() {
        return Result.success(menuService.list().stream().map(Menu::getId));
    }

    @GetMapping
    public Result findAll(@RequestParam(defaultValue = "") String name) {
        return Result.success(menuService.findMenus(name));
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(menuService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Menu> querWrapper = new QueryWrapper<>();
        querWrapper.like("name", name);
        querWrapper.orderByDesc("id");
        return Result.success(menuService.page(new Page<>(pageNum, pageSize),querWrapper));
    }

    @GetMapping("/icons")
    public Result success() {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", Constants.DICT_TYPE_ICON);
        return Result.success(dictMapper.selectList(null));
    }
}

