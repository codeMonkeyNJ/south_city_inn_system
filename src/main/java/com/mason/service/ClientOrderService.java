package com.mason.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mason.domain.PageResult;
import com.mason.domain.dto.*;
import com.mason.domain.vo.OrderCarVO;
import com.mason.domain.vo.OrderPayVO;
import com.mason.domain.vo.OrderSimpleVO;
import com.mason.domain.vo.OrderVO;

public interface ClientOrderService {

    /**
     * 修改购物车
     * @param loginUserId 登录用户id
     * @param orderCarDTO 购物车详情
     */
    OrderCarVO updateOrderCar(Integer loginUserId, OrderCarDTO orderCarDTO) throws JsonProcessingException;

    /**
     * 获取购物车
     * @param loginUserId 登录用户id
     */
    OrderCarVO getOrderCar(Integer loginUserId) throws JsonProcessingException;

    /**
     * 创建订单
     * @param loginUserId 登录用户id
     * @param orderCouponDTO 购买的优惠券信息
     */
    Integer insertOrder(Integer loginUserId, OrderCouponDTO orderCouponDTO) throws JsonProcessingException;

    /**
     * 获取订单列表
     * @param page 页码
     * @param pageSize 页大小
     * @param no 订单编号
     * @param type 订单类型
     * @param dept 部门
     * @param state 订单状态
     * @param eatMode 就餐方式
     * @return 订单列表
     */
    PageResult<OrderSimpleVO> selectOrderList(Integer loginUserId, Integer page, Integer pageSize, String no, Integer type, String dept, Integer state, Integer eatMode);

    /**
     * 根据订单id查询订单
     * @param id 订单id
     * @return 订单信息
     */
    OrderVO selectOrderById(Integer loginUserId, Integer id);

    /**
     * 支付订单
     * @param orderPayDTO 订单支付信息
     * @return 支付信息
     */
    OrderPayVO payOrder(OrderPayDTO orderPayDTO);


    /**
     * 获取订单状态
     * @param id 订单id
     * @return 订单状态
     */
    Integer selectOrderState(Integer loginUserId, Integer id);

    /**
     * 修改订单状态
     * @param orderStateDTO 订单状态信息
     */
    void updateOrderState(Integer loginUserId, OrderStateDTO orderStateDTO);
}
