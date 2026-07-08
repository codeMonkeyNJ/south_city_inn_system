package com.mason.service;

import com.mason.domain.PageResult;
import com.mason.domain.dto.*;
import com.mason.domain.po.Coupon;
import com.mason.domain.vo.MarketingActSimpleVO;
import com.mason.domain.vo.MarketingActivityVO;
import com.mason.domain.vo.MarketingCSimpleVO;
import com.mason.domain.vo.MarketingCouponVO;

import java.util.List;

public interface MarketingService {
    /**
     * 添加优惠券
     * @param marketingCouponDTO 优惠券信息
     */
    void insertCoupon(MarketingCouponDTO marketingCouponDTO);

    /**
     * 修改优惠券
     * @param marketingCouponDTO 优惠券信息
     */
    void updateCoupon(MarketingCouponDTO marketingCouponDTO);

    /**
     * 获取优惠券列表
     * @param page 页码
     * @param pageSize 页大小
     * @param name 优惠券名称
     * @param type 优惠券类型
     * @param state 优惠券状态
     * @return 优惠券列表
     */
    PageResult<MarketingCSimpleVO> selectCouponList(Integer page, Integer pageSize, String name, Integer type, Boolean state);

    /**
     * 根据优惠券id获取优惠券信息(详细详细)
     * @param id 优惠券id
     * @return 优惠券信息
     */
    MarketingCouponVO selectCouponByCId(Integer id);

    /**
     * 根据优惠券id获取优惠券(基本信息)
     * @param id 优惠券id
     * @return 优惠券信息
     */
    Coupon selectCouponBasicByCId(Integer id);

    /**
     * 根据优惠券id列表获取优惠券(简单信息)
     * @param ids 优惠券id列表
     * @return 优惠券信息
     */
    List<MarketingCSimDTO> selectCouponSimpleByCIds(List<Integer> ids);

    /**
     * 修改优惠券状态
     * @param id 优惠券id
     * @param state 优惠券状态
     */
    void updateCouponStateByCId(Integer id, Boolean state);

    /**
     * 批量删除优惠券
     * @param ids 优惠券id列表
     */
    void batchDeleteCoupon(List<Integer> ids);

    /**
     * 添加活动
     * @param marketingActivityDTO 活动信息
     */
    void insertActivity(MarketingActivityDTO marketingActivityDTO);

    /**
     * 修改活动
     * @param marketingActivityDTO 活动信息
     */
    void updateActivity(MarketingActivityDTO marketingActivityDTO);

    /**
     * 获取活动列表
     * @param page 页码
     * @param pageSize 页大小
     * @param name 活动名称
     * @param vip 是否会员活动
     * @param type 活动类型
     * @return 活动列表
     */
    PageResult<MarketingActSimpleVO> selectActivityList(Integer page, Integer pageSize, String name, Boolean vip, Integer type);

    /**
     * 根据活动id获取活动信息
     * @param id 活动id
     * @return 活动信息
     */
    MarketingActivityVO selectActivityByAId(Integer id);

    /**
     * 修改优惠券库存
     * @param couponId 优惠券id
     * @param num 修改数量(正数添加库存,负数减少库存)
     * @return 修改的记录数(用于判断是否修改成功)
     */
    Integer updateCouponInvByCId(Integer couponId, Integer num);

    /**
     * 批量添加优惠券库存
     * @param couponNumList 优惠券id+数量实体类列表
     */
    void batchAddCouponInv(List<OrderCouponDTO> couponNumList);

    /**
     * 添加优惠券到用户券包
     * @param couponId 优惠券id
     * @param loginUserId 用户id
     * @param num 添加数量
     */
    void insertCouponToClient(Integer couponId, Integer loginUserId, Integer num);

    /**
     * 批量添加优惠券到用户券包
     **/
    void batchInsertCouponToClient(List<OrderCouRollbackDTO> clientCouponList);

    /**
     * 修改优惠券核销数量
     * @param couponUseNumList 优惠券使用信息列表
     */
    void updateCouponUseNum(List<MarketingCUseNum> couponUseNumList);

}
