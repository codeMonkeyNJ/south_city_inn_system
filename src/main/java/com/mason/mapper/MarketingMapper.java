package com.mason.mapper;

import com.mason.domain.dto.MarketingCSimDTO;
import com.mason.domain.dto.MarketingCUseNum;
import com.mason.domain.dto.OrderCouponDTO;
import com.mason.domain.po.Activity;
import com.mason.domain.po.ActivityRule;
import com.mason.domain.po.ClientCoupon;
import com.mason.domain.po.Coupon;
import com.mason.domain.vo.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MarketingMapper {
    /**
     * 插入优惠券
     * @param coupon 优惠券
     */
    void insertCoupon(Coupon coupon);

    /**
     * 插入优惠券关联的套餐
     * @param id 优惠券id
     * @param combos 套餐id
     */
    void insertCouponCombo(Integer id, List<Integer> combos);

    /**
     * 插入优惠券关联的菜品
     * @param id 优惠券id
     * @param dishes 菜品id
     */
    void insertCouponDish(Integer id, List<Integer> dishes);

    /**
     * 插入优惠券关联的门店
     * @param id 优惠券id
     * @param depts 部门id
     */
    void insertCouponDept(Integer id, List<Integer> depts);

    /**
     * 根据优惠券id查询优惠券
     * @param id 优惠券id
     * @return 优惠券
     */
    @Select("select id, name, cover, rule, type, discount, derate, threshold, enable_time, disable_time, state, `limit`,overlay, sum, stock, `usage`, price, all_depts, all_dishes, all_combos from coupon where id = #{id}")
    Coupon selectCouponByCId(Integer id);

    /**
     * 修改优惠券
     * @param coupon 优惠券
     */
    @Select("update coupon set name = #{name}, cover = #{cover}, rule = #{rule}, type = #{type}, discount = #{discount}, derate = #{derate}, threshold = #{threshold}, enable_time = #{enableTime}, disable_time = #{disableTime}, state = #{state}, `limit` = #{limit},overlay=#{overlay}, sum = #{sum}, stock = #{stock}, `usage` = #{usage}, price = #{price}, all_depts = #{allDepts}, all_dishes = #{allDishes}, all_combos = #{allCombos} where id = #{id}")
    void updateCoupon(Coupon coupon);

    /**
     * 删除优惠券关联的套餐
     * @param id 优惠券id
     */
    @Select("delete from coupon_combo where coupon_id = #{id}")
    void deleteCouponCombo(Integer id);

    /**
     * 删除优惠券关联的菜品
     * @param id 优惠券id
     */
    @Select("delete from coupon_dish where coupon_id = #{id}")
    void deleteCouponDish(Integer id);

    /**
     * 删除优惠券关联的部门
     * @param id 优惠券id
     */
    @Select("delete from coupon_dept where coupon_id = #{id}")
    void deleteCouponDept(Integer id);

    /**
     * 获取优惠券列表
     * @param name 优惠券名称
     * @param type 优惠券类型
     * @param state 优惠券状态
     * @return 优惠券列表
     */
    Integer countCouponList(String name, Integer type, Boolean state);

    /**
     * 获取优惠券列表
     * @param skip 跳过数量
     * @param pageSize 页大小
     * @param name 优惠券名称
     * @param type 优惠券类型
     * @param state 优惠券状态
     * @return 优惠券列表
     */
    List<MarketingCSimpleVO> selectCouponList(Integer skip, Integer pageSize, String name, Integer type, Boolean state);

    /**
     * 根据优惠券id获取优惠券关联的部门
     * @param id 优惠券id
     * @return 部门列表
     */
    @Select("select cd.dept_id as id, d.name from coupon_dept cd join dept d on cd.dept_id = d.id where cd.coupon_id = #{id}")
    List<MarketingCDeptVO> selectCouponDeptByCId(Integer id);

    /**
     * 根据优惠券id获取优惠券关联的菜品
     * @param id 优惠券id
     * @return 菜品列表
     */
    @Select("select cd.dish_id as id, d.name from coupon_dish cd join dish d on cd.dish_id = d.id where cd.coupon_id = #{id}")
    List<MarketingCDishVO> selectCouponDishByCId(Integer id);

    /**
     * 根据优惠券id获取优惠券关联的套餐
     * @param id 优惠券id
     * @return 套餐列表
     */
    @Select("select cc.combo_id as id, c.name from coupon_combo cc join combo c on cc.combo_id = c.id where cc.coupon_id = #{id}")
    List<MarketingCComboVO> selectCouponComboByCId(Integer id);

    /**
     * 修改优惠券状态
     * @param id 优惠券id
     * @param state 优惠券状态
     */
    @Select("update coupon set state = #{state} where id = #{id}")
    void updateCouponStateByCId(Integer id, Boolean state);

    /**
     * 根据优惠券id列表查询优惠券
     * @param ids 优惠券id列表
     * @return 优惠券
     */
    List<Coupon> selectCouponByCIds(List<Integer> ids);

    /**
     * 批量删除优惠券
     * @param couponIds 优惠券id列表
     */
    void batchDeleteCoupon(List<Integer> couponIds);

    /**
     * 批量删除优惠券关联的部门
     * @param couponIds 优惠券id列表
     */
    void batchDeleteCouponDept(List<Integer> couponIds);

    /**
     * 批量删除优惠券关联的菜品
     * @param couponIds 优惠券id列表
     */
    void batchDeleteCouponDish(List<Integer> couponIds);

    /**
     * 批量删除优惠券关联的套餐
     * @param couponIds 优惠券id列表
     */
    void batchDeleteCouponCombo(List<Integer> couponIds);

    /**
     * 插入活动规则
     * @param activityRule 活动规则
     */
    void insertActivityRule(ActivityRule activityRule);

    /**
     * 插入活动
     * @param activity 活动
     */
    void insertActivity(Activity activity);

    /**
     * 插入活动关联的门店
     * @param id 活动id
     * @param depts 门店id
     */
    void insertActivityDept(Integer id, List<Integer> depts);

    /**
     * 插入活动关联的套餐
     * @param id 活动id
     * @param combos 套餐id
     */
    void insertActivityCombo(Integer id, List<Integer> combos);

    /**
     * 插入活动关联的菜品
     * @param id 活动id
     * @param dishes 菜品id
     */
    void insertActivityDish(Integer id, List<Integer> dishes);

    /**
     * 根据活动id获取活动信息
     * @param id 活动id
     */
    @Select("select id, name, rule_id, start_date, end_date, cover, details_image, type, goods_id from activity where id = #{id}")
    Activity selectActivityByAId(Integer id);

    /**
     * 根据活动规则id获取活动规则信息
     * @param ruleId 活动规则id
     */
    @Select("select id, all_depts, all_dishes, all_combos, vip, overlay, type, buy_num, give_num, discount,goods_type, goods_id, start_time, end_time from activity_rule where id = #{ruleId}")
    ActivityRule selectActivityRuleByRId(Integer ruleId);

    /**
     * 更新活动规则
     * @param activityRule 活动规则
     */
    Integer updateActivityRule(ActivityRule activityRule);

    /**
     * 更新活动信息
     * @param activity 活动信息
     */
    void updateActivity(Activity activity);
    
    /**
     * 删除活动关联的部门
     * @param id 活动id
     */
    @Delete("delete from activity_rule_dept where rule_id = #{id}")
    void deleteActivityDept(Integer id);

    /**
     * 删除活动关联的菜品
     * @param id 活动id
     */
    @Delete("delete from activity_rule_dish where rule_id = #{id}")
    void deleteActivityDish(Integer id);

    /**
     * 删除活动关联的套餐
     * @param id 活动id
     */
    @Delete("delete from activity_rule_combo where rule_id = #{id}")
    void deleteActivityCombo(Integer id);

    /**
     * 统计活动数量
     * @param name 活动名称
     * @param vip 是否会员
     * @param type 活动类型
     * @return 活动列表
     */
    Integer countActivityList(String name, Boolean vip, Integer type);

    /**
     * 获取活动列表
     * @param skip 跳过数量
     * @param pageSize 页大小
     * @param name 活动名称
     * @param vip 是否会员
     * @param type 活动类型
     * @return 活动列表
     */
    List<MarketingActSimpleVO> selectActivityList(Integer skip, Integer pageSize, String name, Boolean vip, Integer type);

    /**
     * 根据活动id获取活动信息(详细)
     * @param id 活动id
     */
    @Select("""
        select a.name,a.cover,a.details_image,a.type as routeType,a.goods_id,a.start_date,a.end_date,
        ar.vip,ar.overlay,ar.once,ar.type as activityType,ar.start_time,ar.end_time,ar.buy_num,ar.give_num,
        ar.discount,ar.goods_type as awardGoodsType, ar.goods_id as awardGoodsId,ar.all_depts,ar.all_dishes,ar.all_combos
        from activity a join activity_rule ar on a.rule_id = ar.id
        where a.id = #{id}
    """)
    MarketingActivityVO selectFullActivityByAId(Integer id);

    /**
     * 根据活动id获取活动关联的规则id
     * @param id 活动id
     */
    @Select("select rule_id from activity where id = #{id}")
    Integer selectActivityRuleIdByAId(Integer id);

    /**
     * 根据活动id获取活动关联的部门
     * @param id 活动id
     */
    @Select("""
        select d.id,d.name
        from activity a join activity_rule_dept ard on a.rule_id = ard.rule_id
        join dept d on ard.dept_id = d.id
        where a.id = #{id}
    """)
    List<MarketingActivityVO.DeptItem> selectActivityDeptsByAId(Integer id);
    
    /**
     * 根据活动id获取活动关联的套餐
     * @param id 活动id
     */
    @Select("""
        select c.id,c.name
        from activity a join activity_rule_combo arc on a.rule_id = arc.rule_id
        join combo c on arc.combo_id = c.id
        where a.id = #{id}
    """)
    List<MarketingActivityVO.ComboItem> selectActivityCombosByAId(Integer id);

    /**
     * 根据活动id获取活动关联的菜品
     * @param id 活动id
     */
    @Select("""
        select d.id,d.name
        from activity a join activity_rule_dish ard on a.rule_id = ard.rule_id
        join dish d on ard.dish_id = d.id
        where a.id = #{id}
    """)
    List<MarketingActivityVO.DishItem> selectActivityDishesByAId(Integer id);

    /**
     * 根据优惠券id获取优惠券价格
     * @param id 优惠券id
     */
    @Select("select id, name, cover, rule, type, discount, derate, threshold, enable_time, disable_time, state, `limit`, overlay, sum, stock, `usage`, price, all_depts, all_dishes, all_combos, update_time, create_time from coupon where id = #{id}")
    Coupon selectCouponPriceByCId(Integer id);

    /**
     * 根据优惠券id获取优惠券简单信息(id+name)
     * @param ids 优惠券id
     */
    List<MarketingCSimDTO> selectCouponSimpleByCIds(List<Integer> ids);

    /**
     * 修改优惠券库存
     * @param couponId 优惠券id
     * @param num 修改数量
     * @return 影响的行数,用于判断修改库存是否成功
     */
    Integer updateCouponInvByCId(Integer couponId, Integer num);

    /**
     * 批量添加优惠券库存
     * @param couponNumList 优惠券id和数量列表
     */
    void batchAddCouponInv(List<OrderCouponDTO> couponNumList);

    /**
     * 插入优惠券到用户
     * @param couponId 优惠券id
     * @param loginUserId 用户id
     * @param num 优惠券数量,list的长度表示优惠券数量
     */
    void insertCouponToClient(Integer couponId, Integer loginUserId, List<Integer> num);

    /**
     * 批量插入优惠券到顾客券包
     * @param clientCoupons 顾客优惠券信息列表
     */
    void batchInsertCouponToClient(List<ClientCoupon> clientCoupons);

    /**
     * 修改优惠券核销数量
     * @param couponUseNumList 优惠券使用信息列表
     */
    void updateCouponUseNum(List<MarketingCUseNum> couponUseNumList);

}
