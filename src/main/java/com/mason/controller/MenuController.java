package com.mason.controller;

import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.dto.*;
import com.mason.domain.vo.*;
import com.mason.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    /**
     *添加菜单类别
     * 权限码：menu-class-insert
     */
    @PostMapping("/class")
    @AuthCode("menu-class-insert")
    public Result insertMenuClass(@RequestBody MenuClassDTO menuClassDTO) {
        menuService.insertMenuClass(menuClassDTO);
        return Result.success();
    }

    /**
     *修改菜单类别
     * 权限码：menu-class-update
     */
    @PutMapping("/class")
    @AuthCode("menu-class-update")
    public Result updateMenuClass(@RequestBody MenuClassDTO menuClassDTO) {
        menuService.updateMenuClass(menuClassDTO);
        return Result.success();
    }

    /**
     * 获取菜单类别列表
     * 权限码：menu-class-select
     */
    @GetMapping("/class")
    @AuthCode("menu-class-select")
    public Result selectMenuClassList() {
        PageResult<MenuClassVO> pageResult = menuService.selectMenuClassList();
        return Result.success(pageResult);
    }

    /**
     * 添加配置项
     * 权限码：menu-config-insert
     */
    @PostMapping("/dish/config")
    @AuthCode("menu-config-insert")
    public Result insertMenuConfig(@RequestBody MenuConfigDTO menuConfigDTO) {
        menuService.insertMenuConfig(menuConfigDTO);
        return Result.success();
    }

    /**
     * 修改配置项
     * 权限码：menu-config-update
     */
    @PutMapping("/dish/config")
    @AuthCode("menu-config-update")
    public Result updateMenuConfig(@RequestBody MenuConfigDTO menuConfigDTO) {
        menuService.updateMenuConfig(menuConfigDTO);
        return Result.success();
    }

    /**
     * 获取菜品配置项列表
     * 权限码：menu-config-select
     */
    @GetMapping("/dish/config")
    @AuthCode("menu-config-select")
    public Result selectMenuConfigList() {
        PageResult<MenuConfigVO> pageResult = menuService.selectMenuConfigList();
        return Result.success(pageResult);
    }

    /**
     * 根据配置选项id查询配置值
     * 权限码：menu-config-value-select-config-id
     */
    @GetMapping("/dish/config/{id}")
    @AuthCode("menu-config-value-select-config-id")
    public Result selectMCVByCId(@PathVariable Integer id) {
        PageResult<MenuConfigValueVO> pageResult = menuService.selectMCVByCId(id);
        return Result.success(pageResult);
    }

    /**
     * 添加菜品
     * 权限码：menu-dish-insert
     */
    @PostMapping("/dish")
    @AuthCode("menu-dish-insert")
    public Result insertMenuDish(@RequestBody MenuDishDTO menuDishDTO) {
        menuService.insertMenuDish(menuDishDTO);
        return Result.success();
    }

    /**
     * 修改菜品
     * 权限码：menu-dish-update
     */
    @PutMapping("/dish")
    @AuthCode("menu-dish-update")
    public Result updateMenuDish(@RequestBody MenuDishDTO menuDishDTO) {
        menuService.updateMenuDish(menuDishDTO);
        return Result.success();
    }

    /**
     * 获取菜品列表
     * 权限码：menu-dish-select
     */
    @GetMapping("/dish")
    @AuthCode("menu-dish-select")
    public Result selectMenuDishList(Integer page, Integer pageSize, String dishName,Boolean state, String className) {
        PageResult<MenuDishSimpleVO> pageResult = menuService.selectMenuDishList(page, pageSize, dishName, state, className);
        return Result.success(pageResult);
    }

    /**
     * 根据菜品id查询菜品
     * 权限码：menu-dish-select-id
     */
    @GetMapping("/dish/{id}")
    @AuthCode("menu-dish-select-id")
    public Result selectMenuDishByDId(@PathVariable Integer id) {
        MenuDishVO menuDishVO = menuService.selectMenuDishByDId(id);//获取菜品基本信息
        return Result.success(menuDishVO);
    }

    /**
     * 添加套餐分组
     * 权限码：menu-combo-group-insert
     */
    @PostMapping("/combo/group")
    @AuthCode("menu-combo-group-insert")
    public Result insertMenuComboGroup(@RequestBody MenuCGroupDTO menuCGroupDTO) {
        menuService.insertMenuComboGroup(menuCGroupDTO);
        return Result.success();
    }

    /**
     * 修改套餐分组
     * 权限码：menu-combo-group-update
     */
    @PutMapping("/combo/group")
    @AuthCode("menu-combo-group-update")
    public Result updateMenuComboGroup(@RequestBody MenuCGroupDTO menuCGroupDTO) {
        menuService.updateMenuComboGroup(menuCGroupDTO);
        return Result.success();
    }

    /**
     * 查询所有分组
     * 权限码：menu-combo-group-select
     */
    @GetMapping("/combo/group")
    @AuthCode("menu-combo-group-select")
    public Result selectMenuComboGroupList() {
        PageResult<MenuComboGSimpleVO> pageResult = menuService.selectMenuComboGroupList();
        return Result.success(pageResult);
    }

    /**
     * 根据分组id获取分组详情
     * 权限码：menu-combo-group-select-id
     */
    @GetMapping("/combo/group/{id}")
    @AuthCode("menu-combo-group-select-id")
    public Result selectMenuComboGroupByGId(@PathVariable Integer id) {
        MenuComboGVO menuComboGVO = menuService.selectMenuComboGroupByGId(id);
        return Result.success(menuComboGVO);
    }

    /**
     * 查询所有套餐分组信息
     */
    @GetMapping("/combo-group-dish")
    public Result selectMenuComboGroupDish(@RequestParam(required = false) List<Integer> groupIds,Integer comboId,Integer dishId) {
        List<MenuGroupComboDishVO> menuComboGroupDishVOS = menuService.selectMenuComboGroupDish(groupIds, comboId, dishId);
        return Result.success(menuComboGroupDishVOS);
    }

    /**
     * 添加套餐
     * 权限码：menu-combo-insert
     */
    @PostMapping("/combo")
    @AuthCode("menu-combo-insert")
    public Result insertMenuCombo(@RequestBody MenuComboDTO menuComboDTO) {
        menuService.insertMenuCombo(menuComboDTO);
        return Result.success();
    }

    /**
     * 修改套餐
     * 权限码：menu-combo-update
     */
    @PutMapping("/combo")
    @AuthCode("menu-combo-update")
    public Result updateMenuCombo(@RequestBody MenuComboDTO menuComboDTO) {
        menuService.updateMenuCombo(menuComboDTO);
        return Result.success();
    }

    /**
     * 获取所有套餐列表
     * 权限码：menu-combo-select
     */
    @GetMapping("/combo")
    @AuthCode("menu-combo-select")
    public Result selectMenuComboList(Integer page, Integer pageSize, String name) {
        PageResult<MenuComboSimpleVO> pageResult = menuService.selectMenuComboList(page, pageSize, name);
        return Result.success(pageResult);
    }

    /**
     * 根据套餐id查询套餐
     * 权限码：menu-combo-select-id
     */
    @GetMapping("/combo/{id}")
    @AuthCode("menu-combo-select-id")
    public Result selectMenuComboByCId(@PathVariable Integer id) {
        MenuComboVO menuComboVO = menuService.selectMenuComboByCId(id);
        return Result.success(menuComboVO);
    }
}
