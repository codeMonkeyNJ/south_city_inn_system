package com.mason.service.impl;

import com.mason.domain.PageResult;
import com.mason.domain.dto.*;
import com.mason.domain.po.*;
import com.mason.domain.vo.*;
import com.mason.exception.BusinessException;
import com.mason.mapper.MenuMapper;
import com.mason.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Override
    public void insertMenuClass(MenuClassDTO menuClassDTO) {
        menuMapper.insertMenuClass(menuClassDTO);
    }

    @Override
    public void updateMenuClass(MenuClassDTO menuClassDTO) {
        menuMapper.updateMenuClass(menuClassDTO);
    }

    @Override
    public PageResult<MenuClassVO> selectMenuClassList() {
        Integer total = menuMapper.countMenuClassList();
        if (total == 0) {return new PageResult<>(0, null);}
        List<MenuClassVO> items =menuMapper.selectMenuClassList();
        return new PageResult<>(total, items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMenuConfig(MenuConfigDTO menuConfigDTO) {
        ConfigOption configOption = new ConfigOption();
        BeanUtils.copyProperties(menuConfigDTO, configOption);
        menuMapper.insertMenuConfig(configOption);//插入菜单配置项
        List<ConfigValue> values = menuConfigDTO.getValues().stream().map(value -> {
            ConfigValue configValue = new ConfigValue();
            BeanUtils.copyProperties(value, configValue);
            configValue.setOptionId(configOption.getId());
            return configValue;
        }).toList();
        menuMapper.batchInsertConfigValue(values);//批量插入菜单配置项值
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuConfig(MenuConfigDTO menuConfigDTO) {
        ConfigOption configOption = menuMapper.selectConfigByCId(menuConfigDTO.getId());
        if (configOption == null){throw new BusinessException("菜单配置项不存在");}
        BeanUtils.copyProperties(menuConfigDTO, configOption);
        menuMapper.updateMenuConfig(configOption);//修改菜单配置项
        //获取菜单配置项值
        List<ConfigValue> oldValues = menuMapper.selectConfigValueByCId(menuConfigDTO.getId());
        List<Integer> oldValueIds = oldValues.stream().map(ConfigValue::getId).toList();
        List<Integer> newValueIds = menuConfigDTO.getValues().stream().map(MenuConfigValueDTO::getId).toList();
        //判断oldValueIds中的元素是否与newValueIds中的元素相同
        if (!new HashSet<>(newValueIds).containsAll(oldValueIds) || oldValueIds.size() != newValueIds.size()){throw new BusinessException("非法操作");}
        //批量修改菜单配置项值
        List<ConfigValue> values = menuConfigDTO.getValues().stream().map(value -> {
            ConfigValue configValue = new ConfigValue();
            BeanUtils.copyProperties(value, configValue);
            configValue.setOptionId(configOption.getId());
            return configValue;
        }).toList();
        menuMapper.batchUpdateConfigValue(values);//批量修改菜单配置项值
    }

    @Override
    public PageResult<MenuConfigVO> selectMenuConfigList() {
        Integer total = menuMapper.countMenuConfigList();
        if (total == 0) {return new PageResult<>(0, null);}
        List<MenuConfigVO> items = menuMapper.selectMenuConfigList();
        return new PageResult<>(total, items);
    }

    @Override
    public PageResult<MenuConfigValueVO> selectMCVByCId(Integer id) {
        Integer total = menuMapper.countMCVByCId(id);
        if (total == 0) {return new PageResult<>(0, null);}
        List<MenuConfigValueVO> items = menuMapper.selectMCVByCId(id);
        return new PageResult<>(total, items);
    }

    @Override
    public List<ConfigValue> selectValueByVIds(List<Integer> valueIds) {
        return menuMapper.selectValueByVIds(valueIds);
    }

    @Override
    public List<ConfigMaterial> selectMaterialDifference(List<ConfigMaterial> configMaterials) {
        return menuMapper.selectMaterialDifference(configMaterials);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMenuDish(MenuDishDTO menuDishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(menuDishDTO, dish);
        try{
            menuMapper.insertMenuDish(dish);//添加菜品
        }catch (DuplicateKeyException e){
            throw new BusinessException("菜品已存在");
        }
        List<GoodsClass> goodClassList = new ArrayList<>();
        GoodsClass goodsClass = new GoodsClass();
        goodsClass.setDishId(dish.getId());
        goodsClass.setGoodsType(0);
        goodsClass.setMenuClassId(0);//默认加入0号类别(未分类)
        goodClassList.add(goodsClass);
        for (Integer classId : menuDishDTO.getClassIds()) {
            if (classId == 0){continue;}
            goodsClass = new GoodsClass();
            goodsClass.setGoodsType(0);
            goodsClass.setDishId(dish.getId());
            goodsClass.setMenuClassId(classId);
            goodClassList.add(goodsClass);
        }
        menuMapper.insertGoodsClass(0,goodClassList);//为菜品添加类别
        List<Formula> formulas = menuDishDTO.getFormula().stream().map(item -> {
            Formula formula = new Formula();
            BeanUtils.copyProperties(item, formula);
            formula.setDishId(dish.getId());
            formula.setMaterialId(item.getId());
            return formula;
        }).toList();
        menuMapper.batchInsertMDFormula(formulas);//批量插入菜品配方
        List<DishConfig> configs = menuDishDTO.getConfigs().stream().map(item -> {
            DishConfig dishConfig = new DishConfig();
            dishConfig.setDishId(dish.getId());
            dishConfig.setOptionId(item.getId());
            return dishConfig;
        }).toList();
        menuMapper.batchInsertMDConfig(configs);//批量插入菜品与配置项的关联关系
        List<ConfigMaterial> configMaterials = new ArrayList<>();
        for (MenuDComfigDTO config : menuDishDTO.getConfigs()) {
            for (MenuDValueDTO value : config.getValues()){
                for (MenuDChangeDTO change : value.getChanges()) {
                    ConfigMaterial configMaterial = new ConfigMaterial();
                    configMaterial.setDishId(dish.getId());
                    configMaterial.setOptionId(config.getId());
                    configMaterial.setValueId(value.getId());
                    configMaterial.setMaterialId(change.getId());
                    configMaterial.setSpread(change.getSpread());
                    configMaterials.add(configMaterial);
                }
            }
        }
        menuMapper.batchInsertMDCM(configMaterials);//批量插入菜品在不同配置下的原料用量变化信息
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuDish(MenuDishDTO menuDishDTO) {
        Dish dish = menuMapper.selectDishByDId(menuDishDTO.getId());//获取菜品信息
        if (dish == null){throw new BusinessException("菜品不存在");}
        BeanUtils.copyProperties(menuDishDTO, dish);
        menuMapper.updateMenuDish(dish);//修改菜单菜品
        menuMapper.deleteGoodsClass(0,dish.getId());//在菜品所属类别中删除该菜品
        List<GoodsClass> goodClassList = new ArrayList<>();
        GoodsClass goodsClass = new GoodsClass();
        goodsClass.setDishId(dish.getId());
        goodsClass.setGoodsType(0);
        goodsClass.setMenuClassId(0);//默认加入0号类别(未分类)
        goodClassList.add(goodsClass);
        for (Integer classId : menuDishDTO.getClassIds()) {
            if (classId == 0){continue;}
            goodsClass = new GoodsClass();
            goodsClass.setGoodsType(0);
            goodsClass.setDishId(dish.getId());
            goodsClass.setMenuClassId(classId);
            goodClassList.add(goodsClass);
        }
        menuMapper.insertGoodsClass(0,goodClassList);//为菜品重新添加类别
        //删除菜品配方
        menuMapper.deleteFormula(dish.getId());
        List<Formula> formulas = menuDishDTO.getFormula().stream().map(item -> {
            Formula formula = new Formula();
            BeanUtils.copyProperties(item, formula);
            formula.setDishId(dish.getId());
            formula.setMaterialId(item.getId());
            return formula;
        }).toList();
        menuMapper.batchInsertMDFormula(formulas);//重新批量插入菜品配方
        //删除菜品与配置项的关联关系
        menuMapper.deleteMDConfig(dish.getId());
        List<DishConfig> configs = menuDishDTO.getConfigs().stream().map(item -> {
            DishConfig dishConfig = new DishConfig();
            dishConfig.setDishId(dish.getId());
            dishConfig.setOptionId(item.getId());
            return dishConfig;
        }).toList();
        menuMapper.batchInsertMDConfig(configs);//重新批量插入菜品与配置项的关联关系
        //删除菜品在不同配置下的原料用量变化信息
        menuMapper.deleteMDCM(dish.getId());
        List<ConfigMaterial> configMaterials = new ArrayList<>();
        for (MenuDComfigDTO config : menuDishDTO.getConfigs()) {
            for (MenuDValueDTO value : config.getValues()){
                for (MenuDChangeDTO change : value.getChanges()) {
                    ConfigMaterial configMaterial = new ConfigMaterial();
                    configMaterial.setDishId(dish.getId());
                    configMaterial.setOptionId(config.getId());
                    configMaterial.setValueId(value.getId());
                    configMaterial.setMaterialId(change.getId());
                    configMaterial.setSpread(change.getSpread());
                    configMaterials.add(configMaterial);
                }
            }
        }
        menuMapper.batchInsertMDCM(configMaterials);//重新批量插入菜品在不同配置下的原料用量变化信息
    }

    @Override
    public PageResult<MenuDishSimpleVO> selectMenuDishList(Integer page, Integer pageSize, String dishName, Boolean state, String className) {
        Integer total = menuMapper.countMenuDishList(dishName, state, className);
        Integer skip = (page - 1) * pageSize;
        List<MenuDishSimpleVO> items = menuMapper.selectMenuDishList(skip, pageSize, dishName, state, className);
        return new PageResult<>(total, items);
    }

    @Override
    public MenuDishVO selectMenuDishByDId(Integer id) {
        MenuDishVO menuDishVO = menuMapper.selectFullDishByDId(id);//获取菜品基本信息
        if (menuDishVO == null){throw new BusinessException("菜品不存在");}
        List<MenuDishFormulaVO> formula = menuMapper.selectDishFormulaByDid(id);//获取菜品配方
        menuDishVO.setFormula(formula);
        List<MenuDishConfigVO> configs = menuMapper.selectDishConfigByDId(id);//获取菜品配置项
        for (MenuDishConfigVO config : configs) {
            List<MenuDishCVVO> values = menuMapper.selectDCVByDIdAndOId(id,config.getOptionId());//获取菜品配置项下的拥有的配置值
            config.setValues(values);
            for (MenuDishCVVO value : values) {
                List<MenuDishCVClVO> changes = menuMapper.selectDCVCByDIdOIdVId(id,config.getOptionId(),value.getValueId());
                value.setChanges(changes);
            }
        }
        menuDishVO.setConfigs(configs);
        return menuDishVO;
    }

    @Override
    public List<Dish> selectDishByDIds(List<Integer> dishIds) {
        return menuMapper.selectDishByDIds(dishIds);
    }

    @Override
    public List<Formula> selectFormulaByDIds(List<Integer> dishIds) {
        return menuMapper.selectFormulaByDIds(dishIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMenuComboGroup(MenuCGroupDTO menuCGroupDTO) {
        Group group = new Group();
        BeanUtils.copyProperties(menuCGroupDTO, group);
        menuMapper.insertMenuComboGroup(group);//插入套餐分组
        if (menuCGroupDTO.getDishes() == null){return;}
        List<GroupDetail> details = menuCGroupDTO.getDishes().stream().map(item -> {
            GroupDetail groupDetail = new GroupDetail();
            groupDetail.setGroupId(group.getId());
            groupDetail.setDishId(item.getId());
            if (item.getNum() == null) {
                groupDetail.setNum(1);
            } else {
                groupDetail.setNum(item.getNum());
            }
            if (item.getRequired() == null) {
                groupDetail.setRequired(false);
            } else {
                groupDetail.setRequired(item.getRequired());
            }
            return groupDetail;
        }).toList();
        menuMapper.batchInsertMGroupDetail(details);//批量插入套餐分组详情
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuComboGroup(MenuCGroupDTO menuCGroupDTO) {
        Group group = menuMapper.selectMenuComboGroupByGId(menuCGroupDTO.getId());
        if (group == null){throw new BusinessException("套餐分组不存在");}
        if (!Objects.equals(menuCGroupDTO.getName(), group.getName())){
            group.setName(menuCGroupDTO.getName());
            menuMapper.updateMenuComboGroup(group);
        }
        menuMapper.deleteMGroupDishByGId(menuCGroupDTO.getId());//删除套餐分组下的所有菜品
        if (menuCGroupDTO.getDishes() == null){return;}
        List<GroupDetail> details = menuCGroupDTO.getDishes().stream().map(item -> {
            GroupDetail groupDetail = new GroupDetail();
            groupDetail.setGroupId(group.getId());
            groupDetail.setDishId(item.getId());
            if (item.getNum() == null) {
                groupDetail.setNum(1);
            } else {
                groupDetail.setNum(item.getNum());
            }
            if (item.getRequired() == null) {
                groupDetail.setRequired(false);
            } else {
                groupDetail.setRequired(item.getRequired());
            }
            return groupDetail;
        }).toList();
        menuMapper.batchInsertMGroupDetail(details);//重新批量插入套餐分组详情
    }

    @Override
    public PageResult<MenuComboGSimpleVO> selectMenuComboGroupList() {
        List<MenuComboGSimpleVO> items = menuMapper.selectMenuComboGroupList();
        return new PageResult<>(items.size(), items);
    }

    @Override
    public MenuComboGVO selectMenuComboGroupByGId(Integer id) {
        MenuComboGVO menuComboGVO = new MenuComboGVO();
        Group group = menuMapper.selectMenuComboGroupByGId(id);
        if (group == null){throw new BusinessException("套餐分组不存在");}
        menuComboGVO.setName(group.getName());
        List<MenuComboGDVO> dishes = menuMapper.selectMenuCGDByGId(id);
        menuComboGVO.setDishes(dishes);
        return menuComboGVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMenuCombo(MenuComboDTO menuComboDTO) {
        Combo combo = new Combo();
        BeanUtils.copyProperties(menuComboDTO, combo);
        BigDecimal defPrice = BigDecimal.ZERO;//套餐原价格
        List<ComboGroup> groups = new ArrayList<>();
        for (MenuComboGDTO group : menuComboDTO.getGroups()) {
            //获取套餐分组下最便宜的菜品的价格
            BigDecimal minPriceDish = menuMapper.selectMinPriceDishByGID(group.getGroupId());
            defPrice = defPrice.add(minPriceDish);
            ComboGroup comboGroup = new ComboGroup();
            BeanUtils.copyProperties(group, comboGroup);
            groups.add(comboGroup);
        }
        combo.setDefPrice(defPrice);
        menuMapper.insertMenuCombo(combo);//插入套餐基本信息
        List<GoodsClass> goodClassList = new ArrayList<>();
        GoodsClass goodsClass = new GoodsClass();
        goodsClass.setComboId(combo.getId());
        goodsClass.setGoodsType(1);
        goodsClass.setMenuClassId(0);//默认加入0号类别(未分类)
        goodClassList.add(goodsClass);
        for (Integer classId : menuComboDTO.getClassIds()) {
            if (classId == 0){continue;}
            goodsClass = new GoodsClass();
            goodsClass.setGoodsType(1);
            goodsClass.setComboId(combo.getId());
            goodsClass.setMenuClassId(classId);
            goodClassList.add(goodsClass);
        }
        menuMapper.insertGoodsClass(1,goodClassList);//为菜品添加类别
        for (ComboGroup group : groups) {group.setComboId(combo.getId());}
        menuMapper.batchInsertComboGroup(groups);//批量插入套餐与分组的对应关系
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuCombo(MenuComboDTO menuComboDTO) {
        Combo combo = new Combo();
        combo.setId(menuComboDTO.getId());
        BeanUtils.copyProperties(menuComboDTO, combo);
        BigDecimal defPrice = BigDecimal.ZERO;//套餐原价格
        List<ComboGroup> groups = new ArrayList<>();
        for (MenuComboGDTO group : menuComboDTO.getGroups()) {
            //获取套餐分组下最便宜的菜品的价格
            BigDecimal minPriceDish = menuMapper.selectMinPriceDishByGID(group.getGroupId());
            defPrice = defPrice.add(minPriceDish);
            ComboGroup comboGroup = new ComboGroup();
            BeanUtils.copyProperties(group, comboGroup);
            groups.add(comboGroup);
        }
        combo.setDefPrice(defPrice);
        Integer updateCount = menuMapper.updateMenuCombo(combo);
        if (updateCount == 0){throw new BusinessException("套餐不存在");}
        menuMapper.deleteGoodsClass(1,combo.getId());//在菜品所属类别中删除该菜品
        List<GoodsClass> goodClassList = new ArrayList<>();
        GoodsClass goodsClass = new GoodsClass();
        goodsClass.setComboId(combo.getId());
        goodsClass.setGoodsType(1);
        goodsClass.setMenuClassId(0);//默认加入0号类别(未分类)
        goodClassList.add(goodsClass);
        for (Integer classId : menuComboDTO.getClassIds()) {
            if (classId == 0){continue;}
            goodsClass = new GoodsClass();
            goodsClass.setGoodsType(1);
            goodsClass.setComboId(combo.getId());
            goodsClass.setMenuClassId(classId);
            goodClassList.add(goodsClass);
        }
        menuMapper.insertGoodsClass(1,goodClassList);//为菜品重新添加类别
        //删除原有的套餐与分组的对应关系
        menuMapper.deleteComboGroupByCId(menuComboDTO.getId());
        for (ComboGroup group : groups) {group.setComboId(combo.getId());}
        menuMapper.batchInsertComboGroup(groups);//重新批量插入套餐与分组的对应关系
    }

    @Override
    public PageResult<MenuComboSimpleVO> selectMenuComboList(Integer page, Integer pageSize, String name) {
        Integer total = menuMapper.countMenuComboList(name);
        Integer skip = (page - 1) * pageSize;
        List<MenuComboSimpleVO> items = menuMapper.selectMenuComboList(skip, pageSize, name);
        return new PageResult<>(total, items);
    }

    @Override
    public List<Combo> selectComboByCIds(List<Integer> comboIds) {
        return menuMapper.selectComboByCIds(comboIds);
    }

    @Override
    public MenuComboVO selectMenuComboByCId(Integer id) {
        MenuComboVO menuComboVO;
        menuComboVO = menuMapper.selectMenuComboByCId(id);//获取套餐基本信息
        List<MenuComboIGVO> groups = menuMapper.selectMCGByCId(id);//根据套餐id列表获取所有分组
        List<Integer> groupIds = groups.stream().map(MenuComboIGVO::getGroupId).toList();
        List<MenuComboIGDDTO> tempDishes = menuMapper.selectMCGDByCId(groupIds);//根据分组id列表获取所有菜品
        List<Integer> dishIds = tempDishes.stream().map(MenuComboIGDDTO::getDishId).toList();
        List<MenuComboIGDCDTO> tempConfigs = menuMapper.selectDishConfigsByDIds(dishIds);//根据菜品id列表获取所有菜品的配置
        List<MenuComboIGDCVDTO> tempValues = menuMapper.selectDishConfigValuesByOIds(dishIds);//根据菜品id列表获取所有菜品的配置值
//        for (MenuComboIGVO group : groups) {
//            List<MenuComboIGDVO> dishes = new ArrayList<>();
//            for (MenuComboIGDDTO tempDish : tempDishes) {
//                if (Objects.equals(group.getGroupId(), tempDish.getGroupId())){
//                    MenuComboIGDVO menuComboIGDVO = new MenuComboIGDVO();
//                    BeanUtils.copyProperties(tempDish, menuComboIGDVO);
//                    dishes.add(menuComboIGDVO);
//                }
//            }
//            for (MenuComboIGDVO dish : dishes) {
//                List<MenuComboIGDCVO> configs = new ArrayList<>();
//                for (MenuComboIGDCDTO tempConfig : tempConfigs) {
//                    if (Objects.equals(dish.getDishId(), tempConfig.getDishId())){
//                        MenuComboIGDCVO menuComboIGDCVO = new MenuComboIGDCVO();
//                        BeanUtils.copyProperties(tempConfig, menuComboIGDCVO);
//                        configs.add(menuComboIGDCVO);
//                    }
//                }
//                for (MenuComboIGDCVO config : configs) {
//                    List<MenuComboIGDCVVO> values = new ArrayList<>();
//                    for (MenuComboIGDCVDTO tempValue : tempValues) {
//                        if (Objects.equals(tempValue.getDishId(), dish.getDishId()) && Objects.equals(tempValue.getOptionId(),config.getOptionId())){
//                            MenuComboIGDCVVO menuComboIGDCVVO = new MenuComboIGDCVVO();
//                            BeanUtils.copyProperties(tempValue,menuComboIGDCVVO);
//                            values.add(menuComboIGDCVVO);
//                        }
//                    }
//                    config.setValues(values);
//                }
//                dish.setConfigs(configs);
//            }
//            group.setDishes(dishes);
//        }
        //先把所有数据转成 Map(上面嵌套循化的优化)
        Map<Integer, List<MenuComboIGDDTO>> dishGroupMap = tempDishes.stream()
                .collect(Collectors.groupingBy(MenuComboIGDDTO::getGroupId));

        Map<Integer, List<MenuComboIGDCDTO>> configDishMap = tempConfigs.stream()
                .collect(Collectors.groupingBy(MenuComboIGDCDTO::getDishId));

        Map<Integer, Map<Integer, List<MenuComboIGDCVDTO>>> valueMap = tempValues.stream()
                .collect(Collectors.groupingBy(
                        MenuComboIGDCVDTO::getDishId,
                        Collectors.groupingBy(MenuComboIGDCVDTO::getOptionId)
                ));

        //开始组装
        for (MenuComboIGVO group : groups) {
            // 直接拿当前分组的菜品
            List<MenuComboIGDDTO> groupDishes = dishGroupMap.getOrDefault(group.getGroupId(), Collections.emptyList());
            List<MenuComboIGDVO> dishVOList = new ArrayList<>();

            for (MenuComboIGDDTO tempDish : groupDishes) {
                MenuComboIGDVO dishVO = new MenuComboIGDVO();
                BeanUtils.copyProperties(tempDish, dishVO);

                // 直接拿当前菜品的配置
                List<MenuComboIGDCDTO> dishConfigs = configDishMap.getOrDefault(tempDish.getDishId(), Collections.emptyList());
                List<MenuComboIGDCVO> configVOList = new ArrayList<>();

                for (MenuComboIGDCDTO tempConfig : dishConfigs) {
                    MenuComboIGDCVO configVO = new MenuComboIGDCVO();
                    BeanUtils.copyProperties(tempConfig, configVO);

                    // 直接拿当前配置的值
                    List<MenuComboIGDCVDTO> configValues = valueMap
                            .getOrDefault(tempDish.getDishId(), Collections.emptyMap())
                            .getOrDefault(tempConfig.getOptionId(), Collections.emptyList());

                    List<MenuComboIGDCVVO> valueVOList = new ArrayList<>();
                    for (MenuComboIGDCVDTO tempValue : configValues) {
                        MenuComboIGDCVVO valueVO = new MenuComboIGDCVVO();
                        BeanUtils.copyProperties(tempValue, valueVO);
                        valueVOList.add(valueVO);
                    }

                    configVO.setValues(valueVOList);
                    configVOList.add(configVO);
                }

                dishVO.setConfigs(configVOList);
                dishVOList.add(dishVO);
            }

            group.setDishes(dishVOList);
        }
        menuComboVO.setGroups(groups);
        return menuComboVO;
    }

    @Override
    public List<MenuGroupComboDishVO> selectMenuComboGroupDish(List<Integer> groupIds, Integer comboId, Integer dishId) {
        //查询符合条件的分组id
        List<Integer> validGroupIds = menuMapper.selectGroupIds(groupIds,comboId,dishId);
        if (validGroupIds.isEmpty()){return new ArrayList<>();}
        List<ComboGroup> comboGroupList = menuMapper.selectComboGroupsByGIds(validGroupIds);//根据分组id列表查询拥有该分组的所有套餐
        List<GroupDetail> groupDetailList = menuMapper.selectGroupDishesByGIds(validGroupIds);//根据分组id列表查询分组内的所有菜品
        //转map集合
        Map<Integer, List<Integer>> groupCombosMap = comboGroupList.stream().collect(Collectors.groupingBy(ComboGroup::getGroupId, Collectors.mapping(ComboGroup::getComboId, Collectors.toList())));
        Map<Integer, List<Integer>> groupDishesMap = groupDetailList.stream().collect(Collectors.groupingBy(GroupDetail::getGroupId, Collectors.mapping(GroupDetail::getDishId, Collectors.toList())));
        //组装返回值
        List<MenuGroupComboDishVO> menuGroupComboDishVOS = new ArrayList<>();
        for (Integer validGroupId : validGroupIds) {
            MenuGroupComboDishVO vo = new MenuGroupComboDishVO();
            vo.setGroupId(validGroupId);
            vo.setComboIds(groupCombosMap.getOrDefault(validGroupId, Collections.emptyList()));
            vo.setDishIds(groupDishesMap.getOrDefault(validGroupId, Collections.emptyList()));
            menuGroupComboDishVOS.add(vo);
        }
        return menuGroupComboDishVOS;
    }
}
