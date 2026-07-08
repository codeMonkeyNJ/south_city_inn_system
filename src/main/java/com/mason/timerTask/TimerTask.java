package com.mason.timerTask;

//import com.mason.service.InventoryExService;
//import com.mason.service.MarketingService;
//import com.mason.service.OrderService;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 定时清除未支付订单
 */
@Slf4j
@Component
public class TimerTask {

//    @Autowired
//    private InventoryExService inventoryExService;
//
//    @Autowired
//    private MarketingService marketingService;
//    /**
//     * 定时删除超时未支付订单
//     *每分钟执行一次
//     */
//    @Scheduled(cron = "0 * * * * ?")
//    @Transactional(rollbackFor = Exception.class)
//    public void clearUnpayOrder(){
//        log.info("开始清理超时订单");
//        List<Order> orders = orderService.selectPayTimeOutOrder();//获取超时未支付的订单
//        if (!orders.isEmpty()){//存在超时未支付的订单
//            List<Integer> orderIds = orders.stream().map(Order::getId).toList();//收集超时未支付的订单id
//            List<Order> goodsOrders = orders.stream().filter(order -> order.getType() == 0).toList();//过滤出点餐订单
//            List<Order> couponOrders = orders.stream().filter(order -> order.getType() == 1).toList();//过滤出优惠券订单
//            if (!goodsOrders.isEmpty()){//存在超时的点餐订单
//                List<Integer> goodsOrderIds = goodsOrders.stream().map(Order::getId).toList();
//                inventoryExService.rollbackRepertory(2, goodsOrderIds);//批量回滚库存
//                orderService.rollbackOrderCoupon(goodsOrderIds);//优惠券批量回滚到用户券包
//            }
//            if (!couponOrders.isEmpty()){//存在超时的优惠券订单
//                //优惠券回滚到库存中
//                List<OrderDetail> orderDetailList = orderService.selectOrderBaseDetailByOIds(couponOrders.stream().map(Order::getId).toList());//批量获取订单明细
//                marketingService.batchAddCouponInv(orderDetailList.stream().map(detail -> new OrderCouponDTO(detail.getGoodsId(), detail.getNum())).toList());//批量添加优惠券库存
//            }
//            orderService.batchCancelOrder(orderIds);//批量取消订单
//        }
//    }
}
