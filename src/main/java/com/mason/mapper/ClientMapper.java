package com.mason.mapper;

import com.mason.domain.po.Client;
import com.mason.domain.po.ClientCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClientMapper {
    /**
     * 根据用户id和优惠券id查询用户优惠券数量
     * @param clientId 用户id
     * @param couponId 优惠券id
     * @return 用户优惠券数量
     */
    @Select("select count(*) from client_coupon where client_id = #{clientId} and coupon_id = #{couponId}")
    Integer selectUserCouponNumByCId(Integer clientId, Integer couponId);
    /**
     * 根据用户id查询用户信息
     * @param clientId 用户id
     * @return 用户信息
     */
    @Select("select points,vip from client where id = #{clientId}")
    Client selectClientByCId(Integer clientId);

    /**
     * 获取用户参与活动的次数
     * @param activityId 活动id
     * @param loginUserId 登录用户id
     * @return 用户活动数量
     */
    @Select("select count(*) from client_activity where activity_id = #{activityId} and client_id = #{loginUserId}")
    Integer countClientActivity(Integer activityId, Integer loginUserId);

    /**
     * 获取用户拥有的优惠券信息
     * @param clientId 登录用户id
     * @return 用户优惠券信息
     */
    @Select("select id, coupon_id, client_id from client_coupon where client_id = #{clientId}")
    List<ClientCoupon> selectUserCouponByCId(Integer clientId);
}
