package com.mason.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mason.domain.PageResult;
import com.mason.domain.dto.OrderCarDTO;
import com.mason.domain.dto.OrderPayDTO;
import com.mason.domain.dto.OrderRefundDTO;
import com.mason.domain.dto.OrderStateDTO;
import com.mason.domain.po.Order;
import com.mason.domain.po.OrderDetail;
import com.mason.domain.vo.OrderPayVO;
import com.mason.domain.vo.OrderCarVO;
import com.mason.domain.vo.OrderSimpleVO;
import com.mason.domain.vo.OrderVO;

import java.util.List;

public interface OrderService {
    /**
     * 修改购物车
     * @param orderCarDTO 购物车信息
     */
    OrderCarVO updateOrderCar(Integer loginUserId, OrderCarDTO orderCarDTO) throws JsonProcessingException;

    /**
     * 获取购物车
     */
    OrderCarVO getOrderCar(Integer loginUserId) throws JsonProcessingException;

    /**
     * 创建订单
     * @param loginUserId 登录用户ID
     * @return 订单ID
     */
    Integer insertOrder(Integer loginUserId) throws JsonProcessingException;

    /**
     * 根据订单id获取订单信息
     * @param id 订单ID
     * @return 订单信息
     */
    OrderVO selectOrderById(Integer loginUserId ,Integer dataCoverage ,Integer id);

    /**
     * 根据订单id获取订单信息(单表)
     * @param id 订单ID
     * @return 订单信息
     */
    Order selectOrderBaseById(Integer id);

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
    PageResult<OrderSimpleVO> selectOrderList(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, String no, Integer type, String dept, Integer state, Integer eatMode);

    /**
     * 支付订单
     * @param orderPayDTO 订单支付信息
     * @return  支付所需要的信息
     */
    OrderPayVO payOrder(OrderPayDTO orderPayDTO);


    /**
     * 获取订单状态
     * @param id 订单ID
     * @return 订单状态
     */
    Integer selectOrderState(Integer id);

    /**
     * 批量获取订单明细(基本信息)
     * @param ids 订单ID列表
     * @return 订单明细列表
     */
    List<OrderDetail> selectOrderBaseDetailByOIds(List<Integer> ids);

    /**
     * 获取超时未支付的订单信息
     * @return 订单支付信息列表
     */
    List<Order> selectPayTimeOutOrder();

    /**
     * 修改订单状态
     * @param orderStateDTO 订单id+订单状态
     */
    void updateOrderState(Integer loginUserId, Integer dataCoverage, OrderStateDTO orderStateDTO);

    /**
     * 批量取消超时未支付的订单
     * @param ids 订单id+订单状态
     */
    void batchCancelOrder(List<Integer> ids);

    /**
     * 订单退款
     * @param orderRefundDTO 订单退款信息
     */
    void refundOrder(Integer loginUserId,Integer dataCoverage,OrderRefundDTO orderRefundDTO);

    /**
     * 优惠券回滚到用户券包
     * @param orderIds 订单id列表
     */
    void rollbackOrderCoupon(List<Integer> orderIds);
}
