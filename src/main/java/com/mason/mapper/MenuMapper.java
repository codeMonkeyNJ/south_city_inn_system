package com.mason.mapper;

import com.mason.domain.dto.MenuClassDTO;
import com.mason.domain.dto.MenuComboIGDCDTO;
import com.mason.domain.dto.MenuComboIGDCVDTO;
import com.mason.domain.dto.MenuComboIGDDTO;
import com.mason.domain.po.*;
import com.mason.domain.vo.*;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MenuMapper {
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
     * 统计类别列表总记录数
     * @return 总记录数
     */
    @Select("select count(*) from menu_class")
    Integer countMenuClassList();

    /**
     * 查询类别列表
     * @return 类别列表
     */
    @Select("select id, name, sort, state from menu_class order by sort")
    List<MenuClassVO> selectMenuClassList();

    /**
     * 添加配置项
     * @param configOption 配置项信息
     */
    @Insert("insert into config_option(name,sort) values(#{name}, #{sort})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insertMenuConfig(ConfigOption configOption);

    /**
     * 批量添加配置项值
     * @param values 配置项值列表
     */
    void batchInsertConfigValue(List<ConfigValue> values);

    /**
     * 根据配置项id查询配置项
     * @param id 配置项id
     * @return 配置项
     */
    @Select("select id, name, sort from config_option where id = #{id}")
    ConfigOption selectConfigByCId(Integer id);

    /**
     * 修改配置项
     * @param configOption 配置项信息
     */
    void updateMenuConfig(ConfigOption configOption);

    /**
     * 根据配置项id查询配置项值列表
     * @param id 配置项id
     * @return 配置项值列表
     */
    @Select("select id,option_id, name, spread from config_value where option_id = #{id}")
    List<ConfigValue> selectConfigValueByCId(Integer id);

    /**
     * 批量修改配置项值
     * @param values 配置项值列表
     */
    void batchUpdateConfigValue(List<ConfigValue> values);

    /**
     * 统计配置项列表总记录数
     * @return 总记录数
     */
    @Select("select count(*) from config_option")
    Integer countMenuConfigList();

    /**
     * 查询配置项列表
     * @return 配置项列表
     */
    @Select("select id, name, sort from config_option order by sort")
    List<MenuConfigVO> selectMenuConfigList();

    /**
     * 根据配置项id统计对应配置项值的数量
     * @param id 配置项id
     * @return 统计对应配置值的数量
     */
    @Select("select count(*) from config_value where option_id = #{id}")
    Integer countMCVByCId(Integer id);

    /**
     * 根据配置项id查询配置项值列表
     * @param id 配置项id
     * @return 配置项值列表
     */
    @Select("select id, name, spread from config_value where option_id = #{id}")
    List<MenuConfigValueVO> selectMCVByCId(Integer id);

    /**
     * 根据配置值id列表查询配置值列表
     * @param valueIds 配置值id列表
     * @return 配置值列表
     */
    List<ConfigValue> selectValueByVIds(List<Integer> valueIds);

    /**
     * 添加菜品
     * @param dish 菜品信息
     */
    @Insert("insert into dish(id, name, price, state, intro, intro_image, cover, sort) values(#{id},#{name},#{price},#{state},#{intro},#{introImage},#{cover},#{sort})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insertMenuDish(Dish dish);

    /**
     * 批量添加菜品配方
     * @param formulas 菜品配方列表
     */
    void batchInsertMDFormula(List<Formula> formulas);

    /**
     * 批量添加菜品与配置项的关联关系
     * @param configs 菜品与配置项的关联关系列表
     */
    void batchInsertMDConfig(List<DishConfig> configs);

    /**
     * 批量添加菜品在不同配置下的原料用量变化信息
     * @param configMaterials 菜品在不同配置下的原料用量变化信息列表
     */
    void batchInsertMDCM(List<ConfigMaterial> configMaterials);

    /**
     * 批量插入商品类别信息
     * @param goodsType 商品类型(0:菜品,1:套餐)
     * @param goodsClassList 商品类别信息列表
     */
    void insertGoodsClass(Integer goodsType, List<GoodsClass> goodsClassList);

    /**
     * 根据菜品id查询菜品信息(单表)
     * @param id 菜品id
     * @return 菜品信息
     */
    @Select("select id, name, price, state, intro, intro_image, cover, sort from dish where id = #{id}")
    Dish selectDishByDId(Integer id);

    /**
     * 修改菜品信息
     * @param dish 菜品信息
     */
    @Update("""
        update dish set
        name = #{name},
        price = #{price},
        state = #{state},
        intro = #{intro},
        intro_image = #{introImage},
        cover = #{cover},
        sort = #{sort}
        where id = #{id}
    """)
    void updateMenuDish(Dish dish);

    /**
     * 删除商品类别信息
     * @param goodsType 商品类型(0:菜品,1:套餐)
     * @param id 商品id
     */
    void deleteGoodsClass(Integer goodsType, Integer id);

    /**
     * 删除菜品配方信息
     * @param id 菜品id
     */
    @Delete("delete from formula where dish_id = #{id}")
    void deleteFormula(Integer id);

    /**
     * 删除菜品与配置项的关联关系
     * @param id 菜品id
     */
    @Delete("delete from dish_config where dish_id = #{id}")
    void deleteMDConfig(Integer id);

    /**
     * 删除菜品在不同配置下的原料用量变化信息
     * @param id 菜品id
     */
    @Delete("delete from config_material where dish_id = #{id}")
    void deleteMDCM(Integer id);

    /**
     * 统计菜品列表总记录数
     * @param dishName 菜品名称
     * @param state 菜品状态
     * @param className 类别名称
     * @return 总记录数
     */
    Integer countMenuDishList(String dishName, Boolean state, String className);

    /**
     * 查询菜品列表
     * @param skip 跳过记录数
     * @param pageSize 每页记录数
     * @param dishName 菜品名称
     * @param state 菜品状态
     * @param className 类别名称
     * @return 菜品列表
     */
    List<MenuDishSimpleVO> selectMenuDishList(Integer skip, Integer pageSize, String dishName, Boolean state, String className);

    /**
     * 根据菜品id查询菜品信息(多表)
     * @param id 菜品id
     * @return 菜品信息
     */
    @Select("""
        select d.cover,d.name,ifnull(temp.className,'未分类') as className,d.intro,d.intro_image,d.price,d.state
        from dish d left join (
            select gc.dish_id,GROUP_CONCAT(mc.name SEPARATOR ',') as className
            from goods_class gc join menu_class mc on gc.menu_class_id = mc.id
            where gc.menu_class_id != 0
            group by gc.dish_id
        ) temp on d.id = temp.dish_id
        where d.id = #{id}
    """)
    MenuDishVO selectFullDishByDId(Integer id);

    /**
     * 根据菜品id列表查询所有菜品的信息
     * @param dishIds 菜品id列表
     * @return 菜品信息
     */
    List<Dish> selectDishByDIds(List<Integer> dishIds);

    /**
     * 根据菜品id查询菜品配方信息
     * @param id 菜品id
     * @return 菜品配方信息
     */
    @Select("""
        select f.material_id as id, m.name, f.num,m.unit,f.step,f.detail
        from formula f join material m on f.material_id = m.id
        where f.dish_id = #{id}
    """)
    List<MenuDishFormulaVO> selectDishFormulaByDid(Integer id);

    /**
     * 根据菜品id查询菜品的配置项信息
     * @param id 菜品id
     * @return 菜品的配置项信息
     */
    @Select("""
        select dc.option_id,co.name
        from dish_config dc join config_option co on dc.option_id = co.id
        where dc.dish_id = #{id}
    """)
    List<MenuDishConfigVO> selectDishConfigByDId(Integer id);

    /**
     * 根据菜品id列表查询所有菜品的所有配置项信息
     * @param dishIds 菜品id列表
     * @return 菜品的配置项信息
     */
    List<MenuComboIGDCDTO> selectDishConfigsByDIds(List<Integer> dishIds);

    /**
     * 根据菜品id列表获取所有菜品的所有配置值信息
     * @param dishIds 菜品id列表
     * @return 菜品的配置值信息列表
     */
    List<MenuComboIGDCVDTO> selectDishConfigValuesByOIds(List<Integer> dishIds);

    /**
     * 获取菜品某配置项拥有的配置值
     * @param dishId 菜品id
     * @param optionId 配置项id
     * @return 菜品某配置项拥有的配置值
     */
    @Select("""
        select cm.value_id,cv.name,cv.spread
        from config_material cm join config_value cv on cm.value_id = cv.id
        where cm.dish_id = #{dishId} and cm.option_id = #{optionId}
        group by cm.value_id
    """)
    List<MenuDishCVVO> selectDCVByDIdAndOId(Integer dishId, Integer optionId);

    /**
     * 根据菜品id、配置项id、配置值id查询菜品某配置项的配置值所对应的物料使用变化量信息
     * @param dishId 菜品id
     * @param optionId 配置项id
     * @param valueId 配置值id
     * @return 菜品某配置项的配置值所对应的物料使用变化量信息
     */
    @Select("""
        select cm.material_id,m.name,cm.spread,m.unit
        from config_material cm join material m on cm.material_id = m.id
        where cm.dish_id = #{dishId} and cm.option_id = #{optionId} and cm.value_id = #{valueId}
    """)
    List<MenuDishCVClVO> selectDCVCByDIdOIdVId(Integer dishId, Integer optionId, Integer valueId);

    /**
     * 插入套餐分组信息
     * @param group 套餐分组信息
     */
    @Insert("insert into `group`(name) values(#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertMenuComboGroup(Group group );

    /**
     * 批量插入套餐分组详情信息
     * @param details 套餐分组详情信息列表
     */
    void batchInsertMGroupDetail(List<GroupDetail> details);

    /**
     * 根据套餐分组id查询套餐分组信息
     * @param id 套餐分组id
     * @return 套餐分组信息
     */
    @Select("select id, name from `group` where id = #{id}")
    Group selectMenuComboGroupByGId(Integer id);

    /**
     * 修改套餐分组信息
     * @param group 套餐分组信息
     */
    @Update("update `group` set name = #{name} where id = #{id}")
    void updateMenuComboGroup(Group group);

    /**
     * 删除套餐分组信息
     * @param id 套餐分组id
     */
    @Delete("delete from group_detail where group_id = #{id}")
    void deleteMGroupDishByGId(Integer id);

    /**
     * 获取套餐分组列表
     */
    @Select("select id,name from `group`")
    List<MenuComboGSimpleVO> selectMenuComboGroupList();

    /**
     * 根据套餐分组id查询套餐分组下的所有菜品信息
     * @param id 套餐分组id
     * @return 菜品信息列表
     */
    @Select("""
        select gd.dish_id as id,d.name,gd.num,gd.required
        from group_detail gd join dish d on gd.dish_id = d.id
        where gd.group_id = #{id}
    """)
    List<MenuComboGDVO> selectMenuCGDByGId(Integer id);

    /**
     * 获取套餐分组下最便宜的菜品的价格
     * @param groupId 套餐分组id
     * @return 最小价格
     */
    @Select("""
        select d.price from group_detail gd join dish d on gd.dish_id = d.id
        where gd.group_id = #{groupId}
        order by d.price
        limit 1
    """)
    BigDecimal selectMinPriceDishByGID(Integer groupId);

    /**
     * 插入套餐信息
     * @param combo 套餐信息
     */
    @Insert("insert into combo (name, cover, def_price, reduce_price, intro, state, sort) VALUE (#{name},#{cover},#{defPrice},#{reducePrice},#{intro},#{state},#{sort})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertMenuCombo(Combo combo);

    /**
     * 批量插入套餐与分组的对应关系
     * @param groups 套餐分组信息列表
     */
    void batchInsertComboGroup(List<ComboGroup> groups);

    /**
     * 修改套餐信息
     * @param combo 套餐信息
     * @return 修改的行数
     */
    @Update("update combo set name = #{name}, cover = #{cover}, def_price = #{defPrice}, reduce_price = #{reducePrice}, intro = #{intro}, state = #{state}, sort = #{sort} where id = #{id}")
    Integer updateMenuCombo(Combo combo);

    /**
     * 根据套餐id删除套餐与分组的对应关系
     * @param id 套餐id
     */
    @Delete("delete from combo_group where combo_id = #{id}")
    void deleteComboGroupByCId(Integer id);

    /**
     * 获取套餐列表
     * @param name 套餐名称
     * @return 套餐列表
     */
    Integer countMenuComboList(String name);

    /**
     * 获取套餐列表
     * @param skip 跳过的行数
     * @param pageSize 每页行数
     * @param name 套餐名称
     * @return 套餐列表
     */
    List<MenuComboSimpleVO> selectMenuComboList(Integer skip, Integer pageSize, String name);

    /**
     * 根据套餐id查询套餐基本信息
     * @param id 套餐id
     * @return 套餐信息
     */
    @Select("select name, cover, def_price, reduce_price, intro, state from combo where id = #{id}")
    MenuComboVO selectMenuComboByCId(Integer id);

    /**
     * 根据套餐id查询套餐分组信息
     * @param id 套餐id
     * @return 套餐分组信息
     */
    @Select("select g.id as groupId,g.name,cg.num from combo_group cg join `group` g on cg.group_id = g.id where cg.combo_id = #{id}")
    List<MenuComboIGVO> selectMCGByCId(Integer id);

    /**
     * 根据套餐分组id列表获取套餐可选的所有菜品信息
     * @param groupIds 套餐分组id列表
     * @return 菜品信息列表
     */
    List<MenuComboIGDDTO> selectMCGDByCId(List<Integer> groupIds);

    /**
     * 根据套餐id列表获取套餐信息
     * @param comboIds 套餐id列表
     * @return 套餐信息列表
     */
    List<Combo> selectComboByCIds(List<Integer> comboIds);

    /**
     * 根据菜品id列表获取菜品配方信息
     * @param dishIds 菜品id列表
     * @return 菜品配方信息列表
     */
    List<Formula> selectFormulaByDIds(List<Integer> dishIds);

    /**
     * 根据配置原料列表,获取原料差额信息
     * @param configMaterials 配置原料列表
     * @return 原料差额信息列表
     */
    List<ConfigMaterial> selectMaterialDifference(List<ConfigMaterial> configMaterials);

    /**
     * 根据套餐id,菜品id,套餐分组id列表获取套餐可选的菜品信息
     * @param groupIds 套餐分组id列表
     * @param comboId 套餐id
     * @param dishId 菜品id
     * @return 菜品信息列表
     */
    List<Integer> selectGroupIds(List<Integer> groupIds, Integer comboId, Integer dishId);

    /**
     * 根据套餐分组id列表获取套餐分组信息
     * @param validGroupIds 套餐分组id列表
     * @return 套餐分组信息列表(分组id+套餐id)
     */
    List<ComboGroup> selectComboGroupsByGIds(List<Integer> validGroupIds);

    /**
     * 根据套餐分组id列表获取套餐分组下的菜品信息
     * @param validGroupIds 套餐分组id列表
     * @return 菜品信息列表(分组id+菜品id)
     **/
    List<GroupDetail> selectGroupDishesByGIds(List<Integer> validGroupIds);
}
