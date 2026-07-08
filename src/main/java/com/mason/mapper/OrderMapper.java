package com.mason.mapper;

import com.mason.domain.dto.*;
import com.mason.domain.po.Order;
import com.mason.domain.po.OrderDetail;
import com.mason.domain.vo.OrderCouponVO;
import com.mason.domain.vo.OrderDetailVO;
import com.mason.domain.vo.OrderSimpleVO;
import com.mason.domain.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单
     * @param orderExDTO 订单信息
     */
    @Insert("insert into `order`(no,type,pickup_num,dept_id,client_type,client_id,activity_id,pay_money,pay_mode,pay_time,expiration_time,state,eat_mode,cause,remark,complete_time,tack_time) " +
            "values(#{no},#{type},#{pickupNum},#{deptId},#{clientType},#{clientId},#{activityId},#{finishPrice},#{payMode},#{payTime},#{expirationTime},#{state},#{eatMode},#{cause},#{remark},#{completeTime},#{tackTime})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insertOrder(OrderExDTO orderExDTO);

    /**
     * 批量插入订单明细
     * @param orderDetails 订单明细列表
     */
    void batchInsertOrderDetail(List<OrderExDetailDTO> orderDetails);

    /**
     * 批量插入订单明细配置
     * @param orderExDetailConfigList 订单明细配置列表
     */
    void batchInsertODConfig(List<OrderExGoodsConfigDTO> orderExDetailConfigList);

    /**
     * 批量插入订单组合明细
     * @param comboDetailList 订单组合明细列表
     */
    void batchInsertOCDetail(List<OrderExComboDetailDTO> comboDetailList);

    /**
     * 批量插入订单组合明细配置
     * @param orderExCDConfigList 订单组合明细配置列表
     */
    void batchInsertOCDConfig(List<OrderExCDConfigDTO> orderExCDConfigList);

    /**
     * 批量删除用户优惠券
     * @param clientId 顾客ID
     * @param couponId 优惠券ID
     */
    @Delete("delete from client_coupon cc where client_id = #{clientId} and coupon_id = #{couponId}  order by cc.id limit 1")
    Integer deleteClientCoupon(Integer clientId, Integer couponId);

    /**
     * 获取订单信息
     * @param id 订单ID
     * @return 订单信息
     */
    @Select("""
        select o.id, o.no, o.type, o.pickup_num, o.dept_id,d.name as deptName, o.client_id, COALESCE(c.username,ui.name) as clientName,
               o.activity_id,a.name as activityName,COALESCE(c.phone,ui.phone),o.pay_money, o.pay_mode, o.pay_time, o.expiration_time,
               o.state, o.eat_mode,o.cause,o.remark, o.complete_time, o.tack_time,o.create_time
        from `order` o left join dept d on o.dept_id = d.id
        left join client c on o.client_type=1 and o.client_id = c.id
        left join user_info ui on o.client_type=0 and o.client_id = ui.user_id
        left join activity a on o.activity_id = a.id
        where o.id = #{id}
    """)
    OrderVO selectOrderById(Integer id);

    /**
     * 获取订单信息(基本信息)
     * @param id 订单ID
     * @return 订单信息
     */
    @Select("""
        select o.id, o.no, o.type, o.pickup_num, o.dept_id,o.client_type, o.client_id,o.activity_id,o.pay_money, o.pay_mode, o.pay_time,
               o.expiration_time,o.state, o.eat_mode,o.cause,o.remark, o.complete_time, o.tack_time,o.create_time
        from `order` o
        where o.id = #{id}
    """)
    Order getBaseOrderByOId(Integer id);

    /**
     * 获取超时未支付的订单列表
     * @return 订单信息
     */
    @Select("select o.id,o.type, o.expiration_time from `order` o where o.state = 0 and o.expiration_time <= #{now}")
    List<Order> selectPayTimeOutOrder(LocalDateTime now);

    /**
     * 获取订单明细
     * @param id 订单ID
     * @return 订单明细列表
     */
    @Select("""
        select od.id, od.goods_type,od.goods_id,od.num,od.couponId,od.pay_money,
            case od.goods_type
                when 0 then d.name
                when 1 then c.name
            end as goodsName,
            case od.goods_type
                when 0 then d.cover
                when 1 then c.cover
            end as cover
        from order_detail od
            left join dish d on od.goods_type = 0 and od.goods_id = d.id
            left join combo c on od.goods_type = 1 and od.goods_id = c.id
        where od.order_id = #{id}
    """)
    List<OrderDetailVO> selectOrderDetailByOId(Integer id);

    /**
     * 获取订单明细(基础信息)
     * @param ids 订单ID列表
     * @return 订单明细列表
     */
    List<OrderDetail> selectOrderBaseDetailByOIds(List<Integer> ids);

    /**
     * 根据订单id列表批量获取订单明细
     * @param orderIds 订单id列表
     * @return 订单明细列表
     */
    List<OrderTDetailDTO>  batchSelectOrderDetailByOId(List<Integer> orderIds);

    /**
     * 获取订单使用的优惠券
     * @param id 订单ID
     * @return 订单优惠券列表
     */
    @Select("""
        select oc.coupon_id as id,c.name
        from order_coupon oc join coupon c on oc.coupon_id = c.id
        where oc.order_id = #{id}
    """)
    List<OrderCouponVO> selectOrderCouponByOId(Integer id);

    /**
     * 批量汇总获取订单优惠券(包含顾客id,优惠券id,数量)
     * @param orderIds 订单ID列表
     * @return 订单优惠券列表
     */
    List<OrderCouRollbackDTO> batchSelectOrderCouponByOIds(List<Integer> orderIds);

    /**
     * 获取订单中菜品明细的配置
     * @param detailIds 订单详情id列表
     * @return 订单菜品明细配置列表
     */
    List<OrderTDConfigDTO> selectODConfigByDIds(List<Integer> detailIds);

    /**
     * 获取订单中套餐明细的菜品信息
     * @param comboDetailIds 订单套餐详情id列表
     * @return 订单套餐明细菜品列表
     */
    List<OrderTCDetailDTO> selectOCDetailByDIds(List<Integer> comboDetailIds);

    /**
     * 获取订单中套餐内菜品的配置信息
     * @param comboInnDetailIds 订单套餐明细id列表
     * @return 订单套餐内菜品配置列表
     */
    List<OrderTCDConfigDTO> selectOCDConfigByDIds(List<Integer> comboInnDetailIds);

    /**
     * 统计订单列表数量
     * @param no 订单编号
     * @param type 订单类型
     * @param dept 部门
     * @param state 订单状态
     * @param eatMode 就餐方式
     * @param validDeptIds 可查询的部门ID列表
     * @return 订单数量
     */
    Integer countOrderList(String no, Integer type, String dept, Integer state, Integer eatMode, List<Integer> validDeptIds);

    /**
     * 统计顾客订单列表数量
     * @param loginUserId 登录用户ID
     * @param no 订单编号
     * @param type 订单类型
     * @param dept 部门
     * @param state 订单状态
     * @param eatMode 就餐方式
     * @return 订单数量
     */
    Integer countClientOrderList(Integer loginUserId, String no, Integer type, String dept, Integer state, Integer eatMode);


    /**
     * 获取订单列表
     * @param skip 跳过数量
     * @param pageSize 页大小
     * @param no 订单编号
     * @param type 订单类型
     * @param dept 部门
     * @param state 订单状态
     * @param eatMode 就餐方式
     * @param validDeptIds 可查询的部门ID列表
     * @return 订单列表
     */
    List<OrderSimpleVO> selectOrderList(Integer skip, Integer pageSize, String no, Integer type, String dept, Integer state, Integer eatMode, List<Integer> validDeptIds);

    /**
     * 获取顾客订单列表
     * @param loginUserId 登录用户ID
     * @param skip 跳过数量
     * @param pageSize 页大小
     * @param no 订单编号
     * @param type 订单类型
     * @param dept 部门
     * @param state 订单状态
     * @param eatMode 就餐方式
     * @return 订单列表
     */
    List<OrderSimpleVO> selectClientOrderList(Integer loginUserId, Integer skip, Integer pageSize, String no, Integer type, String dept, Integer state, Integer eatMode);


    /**
     * 修改订单信息
     * @param order 订单信息
     */
    @Update("update `order` set no=#{no},type=#{type},pickup_num=#{pickupNum},dept_id=#{deptId},client_id=#{clientId},activity_id=#{activityId},pay_money=#{payMoney},pay_mode=#{payMode},pay_time=#{payTime},expiration_time=#{expirationTime},state=#{state},eat_mode=#{eatMode},remark=#{remark},complete_time=#{completeTime},tack_time=#{tackTime},dept_id=#{deptId},dept_id=#{deptId} where id = #{id}")
    void updateOrder(Order order);

    /**
     * 批量取消订单
     * @param ids 订单ID列表
     */
    void batchCancelOrder(List<Integer> ids);

    /**
     * 批量插入订单优惠券
     * @param id 订单ID
     * @param couponIds 优惠券ID列表
     */
    void batchInsertOCoupon(Integer id, List<Integer> couponIds);
}
