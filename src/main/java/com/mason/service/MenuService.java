package com.mason.service;

import com.mason.domain.PageResult;
import com.mason.domain.dto.*;
import com.mason.domain.po.*;
import com.mason.domain.vo.*;

import java.util.List;

public interface MenuService {
    /**
     * 添加类别
     * @param menuClassDTO 类别信息
     */
    void insertMenuClass(MenuClassDTO menuClassDTO);

    /**
     * 修改类别
     * @param menuClassDTO 类别信息
     */
    void updateMenuClass(MenuClassDTO menuClassDTO);

    /**
     * 获取类别列表
     * @return 类别列表
     */
    PageResult<MenuClassVO> selectMenuClassList();

    /**
     * 添加配置项
     * @param menuConfigDTO 配置项信息
     */
    void insertMenuConfig(MenuConfigDTO menuConfigDTO);

    /**
     * 修改配置项
     * @param menuConfigDTO 配置项信息
     */
    void updateMenuConfig(MenuConfigDTO menuConfigDTO);

    PageResult<MenuConfigVO> selectMenuConfigList();

    /**
     * 根据配置项id查询配置项值列表
     * @param id 配置项id
     * @return 配置项值列表
     */
    PageResult<MenuConfigValueVO> selectMCVByCId(Integer id);

    /**
     * 根据配置值id列表查询配置值列表
     * @param valueIds 配置值id列表
     **/
    List<ConfigValue> selectValueByVIds(List<Integer> valueIds);

    /**
     * 根据配置原料列表,获取原料差额信息
     * @param configMaterials 配置原料列表
     */
    List<ConfigMaterial> selectMaterialDifference(List<ConfigMaterial> configMaterials);

    /**
     * 添加菜品
     * @param menuDishDTO 菜品信息
     */
    void insertMenuDish(MenuDishDTO menuDishDTO);

    /**
     * 修改菜品
     * @param menuDishDTO 菜品信息
     */
    void updateMenuDish(MenuDishDTO menuDishDTO);

    /**
     * 获取菜品列表
     * @param page 页码
     * @param pageSize 每页展示记录数
     * @param dishName 菜品名称
     * @param state 状态
     * @param className 菜品类别名称
     * @return 菜品列表
     */
    PageResult<MenuDishSimpleVO> selectMenuDishList(Integer page, Integer pageSize, String dishName, Boolean state, String className);


    /**
     * 根据菜品id查询菜品
     * @param id 菜品id
     * @return 菜品信息
     */
    MenuDishVO selectMenuDishByDId(Integer id);

    /**
     * 根据菜品id列表查询菜品
     * @param dishIds 菜品id列表
     * @return 菜品列表
     */
    List<Dish> selectDishByDIds(List<Integer> dishIds);

    /**
     * 根据菜品id列表批量查询配方信息
     * @param dishIds 菜品id列表
     */
    List<Formula> selectFormulaByDIds(List<Integer> dishIds);

    /**
     * 添加套餐分组
     * @param menuCGroupDTO 套餐分组信息
     */
    void insertMenuComboGroup(MenuCGroupDTO menuCGroupDTO);

    /**
     * 修改套餐分组
     * @param menuCGroupDTO 套餐分组信息
     */
    void updateMenuComboGroup(MenuCGroupDTO menuCGroupDTO);

    /**
     * 获取套餐分组列表
     * @return 套餐分组列表
     */
    PageResult<MenuComboGSimpleVO> selectMenuComboGroupList();

    /**
     * 根据套餐分组id查询套餐分组详情
     * @param id 套餐分组id
     * @return 套餐分组信息
     */
    MenuComboGVO selectMenuComboGroupByGId(Integer id);


    /**
     * 添加套餐
     * @param menuComboDTO 套餐信息
     */
    void insertMenuCombo(MenuComboDTO menuComboDTO);

    /**
     * 修改套餐
     * @param menuComboDTO 套餐信息
     */
    void updateMenuCombo(MenuComboDTO menuComboDTO);

    /**
     * 获取套餐列表
     * @param page 页码
     * @param pageSize 每页展示记录数
     * @param name 套餐名称
     * @return 套餐列表
     */
    PageResult<MenuComboSimpleVO> selectMenuComboList(Integer page, Integer pageSize, String name);

    /**
     * 根据套餐id列表查询套餐
     * @param comboIds 套餐id列表
     * @return 套餐列表
     */
    List<Combo> selectComboByCIds(List<Integer> comboIds);

    /**
     * 根据套餐id查询套餐
     * @param id 套餐id
     * @return 套餐信息
     */
    MenuComboVO selectMenuComboByCId(Integer id);

    /**
     * 根据套餐分组id列表,套餐id列表,菜品id列表查询套餐分组详情
     * @param groupIds 套餐分组id列表
     * @param comboId 套餐id
     * @param dishId 菜品id
     * @return 套餐分组详情
     */
    List<MenuGroupComboDishVO> selectMenuComboGroupDish(List<Integer> groupIds, Integer comboId, Integer dishId);
}
