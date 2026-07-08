package com.mason.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.dto.OrderCarDTO;
import com.mason.domain.dto.OrderPayDTO;
import com.mason.domain.dto.OrderRefundDTO;
import com.mason.domain.dto.OrderStateDTO;
import com.mason.domain.vo.OrderPayVO;
import com.mason.domain.vo.OrderCarVO;
import com.mason.domain.vo.OrderSimpleVO;
import com.mason.domain.vo.OrderVO;
import com.mason.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sys/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 修改购物车
     * @param orderCarDTO 购物车信息
     */
    @PutMapping("/car")
    public Result updateOrderCar(HttpServletRequest request, @RequestBody OrderCarDTO orderCarDTO) throws JsonProcessingException {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        OrderCarVO orderCarVO = orderService.updateOrderCar(loginUserId, orderCarDTO);
        return Result.success(orderCarVO);
    }
    /**
     * 获取购物车
     */
    @GetMapping("/car")
    public Result getOrderCar(HttpServletRequest request) throws JsonProcessingException {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        OrderCarVO orderCarVO = orderService.getOrderCar(loginUserId);
        return Result.success(orderCarVO);
    }
    /**
     * 创建订单
     */
    @PostMapping()
    public Result insertOrder(HttpServletRequest request) throws JsonProcessingException {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer orderId = orderService.insertOrder(loginUserId);
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
    @AuthCode("order-select-id")
    public Result selectOrderById(HttpServletRequest request, @PathVariable Integer id) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        OrderVO orderVO = orderService.selectOrderById(loginUserId,dataCoverage,id);
        return Result.success(orderVO);
    }

    /**
     * 获取订单列表
     * 数据范围
     * 2:仅能获取用户所属部门的订单
     * 1:能获取用户所属部门及子部门的订单
     * 0:能获取所有订单
     */
    @GetMapping
    @AuthCode("order-select")
    public Result getOrderList(HttpServletRequest request,Integer page ,Integer pageSize, String no,Integer type,String dept,Integer state,Integer eatMode){
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
            PageResult<OrderSimpleVO> pageResult = orderService.selectOrderList(loginUserId,dataCoverage,page,pageSize,no,type,dept,state,eatMode);
    return Result.success(pageResult);
    }

    /**
     * 支付订单
     */
    @PutMapping("/pay")
    public Result payOrder(@RequestBody OrderPayDTO orderPayDTO) {
        OrderPayVO orderPayVO = orderService.payOrder(orderPayDTO);
        return Result.success(orderPayVO);
    }

    /**
     * 订单退款
     * 权限码:order-refund
     * 数据范围:
     * 2:仅能退款用户所在部门的订单
     * 1:允许退款用户所在部门及其子部门的订单
     * 0:允许退款所有订单
     */
    @PutMapping("/refund")
    @AuthCode("order-refund")
    public Result refundOrder(HttpServletRequest request, @RequestBody OrderRefundDTO orderRefundDTO) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        orderService.refundOrder(loginUserId,dataCoverage,orderRefundDTO);
        return Result.success();
    }

    /**
     * 获取订单状态
     */
    @GetMapping("/state")
    public Result selectOrderState(Integer id){
        Integer orderState = orderService.selectOrderState(id);
        return Result.success(Map.of("state",orderState));
    }

    //TODO支付成功回调

    /**
     * 修改订单状态
     * 权限码:order-update-state
     * 数据范围
     * 2:仅能取消用户创建的订单
     * 1:能取消用户所属部门及子部门的订单
     * 0:能取消所有订单
     */
    @PutMapping("/state")
    @AuthCode("order-update-state")
    public Result updateOrderState(HttpServletRequest request, @RequestBody OrderStateDTO orderStateDTO){
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        orderService.updateOrderState(loginUserId,dataCoverage,orderStateDTO);
        return Result.success();
    }
}
