package com.mason.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO {
    private Integer id;// 订单id
    private String no;// 订单编号
    private Integer type;// 订单类型(0:点餐,1:优惠券,2:vip)
    private String pickupNum;// 取餐号
    private Integer deptId;// 部门id
    private String deptName;// 部门名称
    private Integer clientId;// 客户id
    private String clientName;// 客户用户名
    private Integer activityId;//参与的活动id
    private String activityName;// 参与的活动名称
    private Integer payMode;// 支付方式(0:微信,1:支付宝,2:银行卡,3:现金)
    private Integer state;// 订单状态(0:待支付,1:已取消,2:制作中,3:待取餐,4:已完成,5:已退款)
    private BigDecimal payMoney;// 订单最终价格
    private Integer eatMode;// 就餐方式(0:堂食,1:自提,2:外送)
    private String phone;// 手机号
    private String cause;// 取消退款原因
    private String remark;// 订单备注
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;// 订单创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;// 支付时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationTime;// 支付超时时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;// 制作完成时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tackTime;// 取餐时间
    private List<OrderCouponVO> coupons;//使用的优惠券id列表
    private List<OrderDetailVO> orderDetails;//订单明细
}
