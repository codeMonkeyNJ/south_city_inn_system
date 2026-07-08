package com.mason.domain.dto;

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
public class OrderExDTO {
    private Integer id;// 订单id
    private String no;// 订单编号
    private Integer type;// 订单类型(0:点餐,1:优惠券,2:vip)
    private String pickupNum;// 取餐号
    private Integer deptId;// 部门id
    private Integer clientType;// 客户类型(0:系统用户,1:客户端用户)
    private Integer clientId;// 客户id
    private Integer activityId;//参与的活动id
    private BigDecimal finishPrice;// 订单最终价格
    private Integer payMode;// 支付方式(0:微信,1:支付宝,2:银行卡,3:现金)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;// 支付时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationTime;// 支付超时时间
    private Integer state;// 订单状态(0:待支付,1:已取消,2:制作中,3:待取餐,4:已完成,5:已退款)
    private Integer eatMode;// 就餐方式(0:堂食,1:自提,2:外送)
    private String cause;// 退款原因
    private String remark;// 订单备注
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;// 制作完成时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tackTime;// 取餐时间
    private List<Integer> couponIds;//使用的优惠券id列表
    private List<OrderExDetailDTO> orderDetails;
}
