package com.mason.service;

import com.mason.domain.po.Client;
import com.mason.domain.po.ClientCoupon;

import java.util.List;

public interface ClientService {
    /**
     * 根据用户和优惠券id获取用户对该优惠券的持有数量
     * @param clientId 顾客id
     * @param couponId 优惠券id
     * @return 用户信息
     */
    Integer selectUserCouponNumByCId(Integer clientId, Integer couponId);

    /**
     * 根据用户id获取用户拥有的优惠券信息
     * @param clientId 顾客id
     * @return 用户信息
     */
    List<ClientCoupon> selectUserCouponByCId(Integer clientId);


    /**
     * 根据用户id获取用户信息
     * @param clientId 登录用户id
     * @return 用户信息
     */
    Client selectClientByCId(Integer clientId);

    /**
     * 检查用户是否参与某活动
     * @param activityId 活动id
     * @param loginUserId 登录用户id
     * @return 用户参与活动信息
     */
    boolean checkparticipated(Integer activityId, Integer loginUserId);
}
