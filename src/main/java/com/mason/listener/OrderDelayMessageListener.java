package com.mason.listener;

import com.mason.domain.dto.OrderCouponDTO;
import com.mason.domain.po.Order;
import com.mason.domain.po.OrderDetail;
import com.mason.service.InventoryExService;
import com.mason.service.MarketingService;
import com.mason.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {
    private final OrderService orderService;
    private final InventoryExService inventoryExService;
    private final MarketingService marketingService;

    /**
     * 订单延迟消息监听
     * @param orderId 订单ID
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "order_delay_dlx_queue"),
            exchange = @Exchange(name = "order_delay_dlx_exchange"),
            key = "order_delay_key"
    ))
    @Transactional(rollbackFor = Exception.class)
    public void listenOrderDelayMessage(Integer orderId) {
        log.info("监听到延迟订单消息,订单ID:{}",orderId);
        //判断订单当前状态
        Order order = orderService.selectOrderBaseById(orderId);
        if (order.getState() == 0) {//订单状态为待支付
            //取消订单
            switch (order.getType()){
                case 0://点餐订单
                    inventoryExService.rollbackRepertory(2, List.of(orderId));//批量回滚库存
                    orderService.rollbackOrderCoupon(List.of(orderId));//批量优惠券批量回滚到用户券包
                    break;
                case 1://优惠券订单
                    List<OrderDetail> orderDetailList = orderService.selectOrderBaseDetailByOIds(List.of(orderId));//批量获取订单明细
                    marketingService.batchAddCouponInv(orderDetailList.stream().map(detail -> new OrderCouponDTO(detail.getGoodsId(), detail.getNum())).toList());//批量添加优惠券库存
            }
            orderService.batchCancelOrder(List.of(orderId));//批量取消订单
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "error_queue"),
            exchange = @Exchange(name = "error.direct"),
            key = "error"
    ))
    public void listenErrorMessage(Object msg) {
        log.info("监听到错误消息:{}",msg);
    }
}
