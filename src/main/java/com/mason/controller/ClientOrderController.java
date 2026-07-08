package com.mason.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.dto.*;
import com.mason.domain.vo.OrderCarVO;
import com.mason.domain.vo.OrderPayVO;
import com.mason.domain.vo.OrderSimpleVO;
import com.mason.domain.vo.OrderVO;
import com.mason.service.ClientOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/client/order")
public class ClientOrderController {
    @Autowired
    private ClientOrderService clientOrderService;

    /**
     * 修改购物车
     * @param orderCarDTO 购物车数据
     * @return 购物车数据
     */
    @PutMapping("/car")
    public Result updateOrderCar(HttpServletRequest request, @RequestBody OrderCarDTO orderCarDTO) throws JsonProcessingException {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        OrderCarVO orderCarVO = clientOrderService.updateOrderCar(loginUserId,orderCarDTO);
        return Result.success(orderCarVO);
    }
    /**
     * 获取购物车
     */
    @GetMapping("/car")
    public Result getOrderCar(HttpServletRequest request) throws JsonProcessingException {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        OrderCarVO orderCarVO = clientOrderService.getOrderCar(loginUserId);
        return Result.success(orderCarVO);
    }
    /**
     * 创建订单
     */
    @PostMapping()
    public Result insertOrder(HttpServletRequest request,@RequestBody(required = false) OrderCouponDTO orderCouponDTO) throws JsonProcessingException {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer orderId = clientOrderService.insertOrder(loginUserId,orderCouponDTO);
        return Result.success(Map.of("orderId",orderId));
    }

    /**
     * 根据订单id查询订单
     * 权限码:order-select-id
     * 数据范围:
     * 2:只能查询登录用户的订单
     * 1:允许查询用户所属部门及其子部门的订单
     * 0:允许查询所有订单
     */
    @GetMapping("/{id}")
    public Result selectOrderById(HttpServletRequest request, @PathVariable Integer id) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        OrderVO orderVO = clientOrderService.selectOrderById(loginUserId,id);
        return Result.success(orderVO);
    }

    /**
     * 获取订单列表
     * 数据范围
     * 只能获取本人的订单
     */
    @GetMapping
    @AuthCode("order-select")
    public Result getOrderList(HttpServletRequest request,Integer page ,Integer pageSize, String no,Integer type,String dept,Integer state,Integer eatMode){
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        PageResult<OrderSimpleVO> pageResult = clientOrderService.selectOrderList(loginUserId,page,pageSize,no,type,dept,state,eatMode);
        return Result.success(pageResult);
    }

    /**
     * 支付订单
     */
    @PutMapping("/pay")
    public Result payOrder(@RequestBody OrderPayDTO orderPayDTO) {
        OrderPayVO orderPayVO = clientOrderService.payOrder(orderPayDTO);
        return Result.success(orderPayVO);
    }

    /**
     * 获取订单状态
     */
    @GetMapping("/state")
    public Result selectOrderState(HttpServletRequest request,Integer id){
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer orderState = clientOrderService.selectOrderState(loginUserId,id);
        return Result.success(Map.of("state",orderState));
    }

    /**
     * 修改订单状态
     * 权限码:order-update-state
     * 数据范围
     * 登录用户创建的订单
     */
    @PutMapping("/state")
    @AuthCode("order-update-state")
    public Result updateOrderState(HttpServletRequest request, @RequestBody OrderStateDTO orderStateDTO){
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        clientOrderService.updateOrderState(loginUserId,orderStateDTO);
        return Result.success();
    }

}
