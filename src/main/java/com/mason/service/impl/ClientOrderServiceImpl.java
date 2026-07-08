package com.mason.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mason.domain.PageResult;
import com.mason.domain.dto.*;
import com.mason.domain.po.*;
import com.mason.domain.vo.*;
import com.mason.exception.AuthorityException;
import com.mason.exception.BusinessException;
import com.mason.mapper.OrderMapper;
import com.mason.service.*;
import com.mason.utils.UniqueNo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ClientOrderServiceImpl  implements ClientOrderService {
    @Autowired
    private MarketingService marketingService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UniqueNo uniqueNo;
    @Autowired
    private DeptService deptService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private InventoryExService inventoryExService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderService orderService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCarVO updateOrderCar(Integer loginUserId, OrderCarDTO orderCarDTO) throws JsonProcessingException {
        boolean isLogin = loginUserId != -1;
        Client client = isLogin?clientService.selectClientByCId(loginUserId):null;
        if (!isLogin){orderCarDTO.setCouponIds(null);}//未登录不能使用优惠券
        if (orderCarDTO.getType() == 1){//优惠券订单
            orderCarDTO.setDeptId(null);
            orderCarDTO.setEatMode(null);
            orderCarDTO.setCouponIds(null);
            if (!isLogin){
                throw new BusinessException("请先登录");
            }
            if (orderCarDTO.getOrderDetails().size() != 1 || orderCarDTO.getOrderDetails().get(0).getGoodsType()!=2){
                throw new BusinessException("订单明细异常");
            }
        } else if (orderCarDTO.getType() == 0){//普通订单
            if (orderCarDTO.getDeptId() == null){throw new BusinessException("请选择门店");}
            if (orderCarDTO.getEatMode() == null){throw new BusinessException("请选择就餐方式");}
        }
        //从orderCarDTO中拷贝属性到orderCarVO
        //写法一:代码少,性能差
        //String orderCarDTOJson = objectMapper.writeValueAsString(orderCarDTO);
        //OrderCarVO orderCarVO = objectMapper.readValue(orderCarDTOJson, OrderCarVO.class);
        //写法二:代码多,提升点性能
        OrderCarVO orderCarVO = new OrderCarVO();
        List<OrderCarDetailVO> orderDetailList = new ArrayList<>();
        for (OrderCarDetailDTO orderDetail : orderCarDTO.getOrderDetails()) {
            OrderCarDetailVO orderCarDetailVO = new OrderCarDetailVO();
            BeanUtils.copyProperties(orderDetail, orderCarDetailVO);
            List<OrderCarGoodsConfigVO> goodsConfigs = new ArrayList<>();
            for (OrderCarGoodsConfigDTO config : orderDetail.getConfigs()) {
                OrderCarGoodsConfigVO orderCarGoodsConfigVO = new OrderCarGoodsConfigVO();
                BeanUtils.copyProperties(config, orderCarGoodsConfigVO);
                goodsConfigs.add(orderCarGoodsConfigVO);
            }
            orderCarDetailVO.setConfigs(goodsConfigs);
            List<OrderCarComboDetailVO> comboDetails = new ArrayList<>();
            for (OrderComboDetailDTO comboDetail : orderDetail.getComboDetails()) {
                OrderCarComboDetailVO orderCarComboDetailVO = new OrderCarComboDetailVO();
                BeanUtils.copyProperties(comboDetail, orderCarComboDetailVO);
                goodsConfigs = new ArrayList<>();
                for (OrderGoodsConfigDTO config : comboDetail.getConfigs()) {
                    OrderCarGoodsConfigVO orderCarGoodsConfigVO = new OrderCarGoodsConfigVO();
                    BeanUtils.copyProperties(config, orderCarGoodsConfigVO);
                    goodsConfigs.add(orderCarGoodsConfigVO);
                }
                orderCarComboDetailVO.setConfigs(goodsConfigs);
                comboDetails.add(orderCarComboDetailVO);
            }
            orderCarDetailVO.setComboDetails(comboDetails);
            orderDetailList.add(orderCarDetailVO);
        }
        BeanUtils.copyProperties(orderCarDTO, orderCarVO);
        orderCarVO.setOrderDetails(orderDetailList);
        orderCarVO.setOriginalPrice(BigDecimal.ZERO);
        List<OrderCarDetailVO> orderDetails = orderCarVO.getOrderDetails();//获取订单明细
        if (orderDetails.isEmpty()){
            return orderCarVO;
        }
        if (orderCarVO.getType() == 1){//优惠券订单
            Integer couponId = orderDetails.get(0).getGoodsId();
            Coupon coupon = marketingService.selectCouponBasicByCId(couponId);//获取优惠券信息
            if (coupon == null){throw new BusinessException("优惠券不存在");}
            Integer limit = coupon.getLimit();
            if(limit != null && limit > 0){//优惠券有数量限制
                Integer hasNum = clientService.selectUserCouponNumByCId(loginUserId, couponId);//获取用户拥有该优惠券数量
                if(hasNum + orderDetails.get(0).getNum() > limit){throw new BusinessException("该优惠券已达持有上限,请先使用已有优惠券");}
            }
            orderCarVO.getOrderDetails().get(0).setOriginalPrice(coupon.getPrice().multiply(BigDecimal.valueOf(orderDetails.get(0).getNum())));
            orderCarVO.getOrderDetails().get(0).setFinishPrice(orderCarVO.getOrderDetails().get(0).getOriginalPrice());
            orderCarVO.setOriginalPrice(coupon.getPrice().multiply(BigDecimal.valueOf(orderDetails.get(0).getNum())));//优惠券价格*数量
            orderCarVO.setFinishPrice(orderCarVO.getOriginalPrice());
        }else if (orderCarVO.getType() == 0){//订单类型为点餐订单
            List<Integer> dishIds = new ArrayList<>();//菜品ID列表
            List<Integer> comboIds = new ArrayList<>();//套餐ID列表
            List<Integer> valueIds = new ArrayList<>();//配置值id列表
            Map<Integer,Dish> dishMap; //菜品map集合,map<菜品id,菜品信息>
            Map<Integer,Combo> comboMap;//套餐map集合,map<套餐id,套餐信息>
            Map<Integer,ConfigValue> valueMap;//配置值map集合,map<配置值id,配置值信息>
            MarketingActivityVO activity;//营销活动信息
            //收集订单明细中的所有菜品,套餐,配置项并获取他们的价格
            for (OrderCarDetailVO orderDetail : orderDetails) {//遍历订单明细
                if (orderDetail.getGoodsType() == 0){//订单明细为菜品
                    dishIds.add(orderDetail.getGoodsId());//收集菜品ID
                    for (OrderCarGoodsConfigVO config : orderDetail.getConfigs()) {//遍历菜品配置明细
                        valueIds.add(config.getValueId());//收集所有配置值id
                    }
                }else if (orderDetail.getGoodsType() == 1){//订单明细为套餐
                    comboIds.add(orderDetail.getGoodsId());//收集套餐ID
                    for (OrderCarComboDetailVO comboDetail : orderDetail.getComboDetails()) {//遍历套餐明细
                        dishIds.add(comboDetail.getDishId());//收集菜品ID
                        for (OrderCarGoodsConfigVO config : comboDetail.getConfigs()) {//遍历菜品配置明细
                            valueIds.add(config.getValueId());//收集所有配置值id
                        }
                    }
                }
            }
            valueIds = valueIds.stream().distinct().toList();//去重
            List<ConfigValue> configValues = menuService.selectValueByVIds(valueIds);//获取配置项值与对应差价
            valueMap = configValues.stream().collect(Collectors.toMap(ConfigValue::getId, value->value));//将配置信息转为Map集合，key为配置值Id，value为配置值信息
            dishIds = dishIds.stream().distinct().toList();//去重
            List<Dish> dishes = menuService.selectDishByDIds(dishIds);//获取菜品与对应售价
            dishMap = dishes.stream().collect(Collectors.toMap(Dish::getId, dish->dish));//将菜品转为Map集合，key为菜品id，value为菜品信息
            List<Combo> combos = menuService.selectComboByCIds(comboIds);//获取套餐与对应优惠
            comboMap = combos.stream().collect(Collectors.toMap(Combo::getId, combo->combo));//将套餐转为Map集合，key为套餐id，value为套餐信息

            //计算原始价格并完善返回信息
            for (OrderCarDetailVO orderCarDetailVO : orderCarVO.getOrderDetails()) {
                if (orderCarDetailVO.getGoodsType() == 0){//订单明细菜品
                    BigDecimal price = dishMap.get(orderCarDetailVO.getGoodsId()).getPrice();//基础价格
                    for (OrderCarGoodsConfigVO config : orderCarDetailVO.getConfigs()) {
                        price = price.add(valueMap.get(config.getValueId()).getSpread());//加上配置差价
                        config.setName(valueMap.get(config.getValueId()).getName());//设置配置名称
                    }
                    price = price.multiply(new BigDecimal(orderCarDetailVO.getNum()));//最终菜品明细的价格=单个菜品的价格 * 菜品数量
                    orderCarDetailVO.setOriginalPrice(price);
                    orderCarDetailVO.setGoodsName(dishMap.get(orderCarDetailVO.getGoodsId()).getName());//设置菜品名称
                    orderCarDetailVO.setCover(dishMap.get(orderCarDetailVO.getGoodsId()).getCover());//设置菜品图片
                }else if(orderCarDetailVO.getGoodsType() == 1){//订单明细套餐
                    BigDecimal comboPrice = BigDecimal.ZERO;//实际价格
                    for (OrderCarComboDetailVO comboDetail : orderCarDetailVO.getComboDetails()) {
                        comboDetail.setDishName(dishMap.get(comboDetail.getDishId()).getName());//设置套餐中菜品的名称
                        BigDecimal dishPrice = dishMap.get(comboDetail.getDishId()).getPrice();//获取套餐中的菜品价格
                        for (OrderCarGoodsConfigVO config : comboDetail.getConfigs()) {
                            dishPrice = dishPrice.add(valueMap.get(config.getValueId()).getSpread());//菜品价格加上菜品的配置差价
                            config.setName(valueMap.get(config.getValueId()).getName());//设置配置项名称
                        }
                        dishPrice = dishPrice.multiply(new BigDecimal(comboDetail.getNum()));//菜品价格乘以菜品数量
                        comboPrice = comboPrice.add(dishPrice);//累加最终菜品价格得到套餐原价
                    }
                    comboPrice = comboPrice.subtract(comboMap.get(orderCarDetailVO.getGoodsId()).getReducePrice());//单个套餐的价格=套餐原价格-套餐优惠价格
                    comboPrice = comboPrice.multiply(new BigDecimal(orderCarDetailVO.getNum()));
                    orderCarDetailVO.setOriginalPrice(comboPrice);//最终套餐明细的价格=单个套餐的价格 * 套餐数量
                    orderCarDetailVO.setGoodsName(comboMap.get(orderCarDetailVO.getGoodsId()).getName());//设置菜品名称
                    orderCarDetailVO.setCover(comboMap.get(orderCarDetailVO.getGoodsId()).getCover());//设置菜品图片
                }
            }
            //计算订单原价总金额
            for (OrderCarDetailVO orderCarDetailVO : orderCarVO.getOrderDetails()) {
                orderCarVO.setOriginalPrice(orderCarVO.getOriginalPrice().add(orderCarDetailVO.getOriginalPrice()));
            }
            log.info("原始价格计算完成");

            //计算参与活动后的订单明细
            if(orderCarVO.getActivityId() != null){//参与了营销活动
                activity = marketingService.selectActivityByAId(orderCarVO.getActivityId());//获取活动详情
                if(activity == null){throw new BusinessException("该营销活动不存在");}
                if (!activity.getOverlay()){orderCarVO.setCouponIds(null);}//判断该活动是否可叠加优惠券,若不可叠加则清空优惠券ID列表
                if (LocalDate.now().isBefore(activity.getStartDate()) || LocalDate.now().isAfter(activity.getEndDate())){throw new BusinessException("不在活动有效期");}
                if (activity.getVip() &&(client==null || !client.getVip())){throw new BusinessException("该营销活动仅限VIP用户");}
                if (!activity.getAllDepts()){
                    List<Integer> deptIds = activity.getDepts().stream().map(MarketingActivityVO.DeptItem::getId).toList();
                    if (!deptIds.contains(orderCarVO.getDeptId())){throw new BusinessException("当前门店不参与该活动");}
                }
                if (activity.getOnce() && clientService.checkparticipated(orderCarVO.getActivityId(),loginUserId)){throw new BusinessException("当前用户已参与过该活动");}
                boolean isHit = false;//标记活动是否命中,命中后开始寻找奖励商品
                boolean isFinish = activity.getActivityType() == 2 && (LocalTime.now(ZoneId.systemDefault()).isBefore(activity.getStartTime()) || LocalTime.now(ZoneId.systemDefault()).isAfter(activity.getEndTime()));//标记活动是否已处理完成
                //时段优惠活动不在规定时间段内,直接退出
                //活动校验成功,开始计算活动后的订单明细
                Map<Integer,Map<Integer,Integer>> countGoodsMap = new HashMap<>();//统计订单中各商品的数量,map<商品类型(1菜品2套餐),map<商品id,数量>>
                countGoodsMap.put(0,new HashMap<>());//初始化菜品map
                countGoodsMap.put(1,new HashMap<>());//初始化套餐map
                List<Integer> validDishIds = activity.getDishes().stream().map(MarketingActivityVO.DishItem::getId).toList();//参与活动的菜品id
                List<Integer> validComboIds = activity.getCombos().stream().map(MarketingActivityVO.ComboItem::getId).toList();//参与活动的套餐id
                for (OrderCarDetailVO orderCarDetailVO : orderCarVO.getOrderDetails()) {
                    if (isFinish){break;}
                    Integer goodsId = orderCarDetailVO.getGoodsId();//商品id
                    Integer goodsType = orderCarDetailVO.getGoodsType();//商品类型
                    Integer num = orderCarDetailVO.getNum();//商品数量
                    BigDecimal payMoney = orderCarDetailVO.getOriginalPrice();//明细金额
                    BigDecimal oncePrice = payMoney.divide(BigDecimal.valueOf(num),2, RoundingMode.HALF_UP);//1份商品的价格
                    if(isHit && Objects.equals(goodsType, activity.getAwardGoodsType()) && Objects.equals(goodsId, activity.getAwardGoodsId())){
                        //命中奖励商品
                        switch (activity.getActivityType()){
                            case 0: //买赠活动
                                orderCarDetailVO.setFinishPrice(payMoney.subtract(oncePrice));//减去1份商品的价格
                                break;
                            case 1: //买折活动
                                BigDecimal discountPrice = oncePrice.multiply(activity.getDiscount());//单份折扣价
                                orderCarDetailVO.setFinishPrice(payMoney.subtract(oncePrice).add(discountPrice));//应付金额 = 明细价格-单份原价+折扣价
                                break;
                        }
                        isFinish = true;
                    }else if(!isHit){
                        //未命中活动
                        //统计菜品数量(有则累加、无则新增)
                        countGoodsMap.get(goodsType).merge(goodsId, num, Integer::sum);
                        if((goodsType==0 && (activity.getAllDishes() || validDishIds.contains(goodsId))) ||
                                (goodsType==1 && (activity.getAllCombos() || validComboIds.contains(goodsId)))){//判断该商品是否参与活动
                            switch (activity.getActivityType()){//判断活动类型
                                case 0://买赠活动
                                    //判断菜品数量是否满足活动要求
                                    if (countGoodsMap.get(goodsType).get(goodsId) >= activity.getBuyNum()){//活动命中
                                        isHit = true;//标记活动命中
                                        if (activity.getAwardGoodsId() == null){//买送商品一致
                                            activity.setAwardGoodsId(goodsId);
                                            activity.setAwardGoodsType(goodsType);
                                            if (countGoodsMap.get(goodsType).get(goodsId) > activity.getBuyNum()){//菜品数量超过要求
                                                orderCarDetailVO.setFinishPrice(payMoney.subtract(oncePrice));//应付金额 = 明细价格-单份原价
                                                isFinish = true;//标记活动处理完成
                                            }
                                        }
                                    }
                                    break;
                                case 1://买折活动
                                    //判断菜品数量是否满足活动要求
                                    if (countGoodsMap.get(goodsType).get(goodsId) >= activity.getBuyNum()){//活动命中
                                        isHit = true;//标记活动命中
                                        if (activity.getAwardGoodsId() == null){//买折商品一致
                                            activity.setAwardGoodsId(goodsId);
                                            activity.setAwardGoodsType(goodsType);
                                            if (countGoodsMap.get(goodsType).get(goodsId) > activity.getBuyNum()){//菜品数量超过要求
                                                BigDecimal discountPrice = oncePrice.multiply(activity.getDiscount());//单份折扣价
                                                orderCarDetailVO.setFinishPrice(payMoney.subtract(oncePrice).add(discountPrice));//应付金额 = 明细价格-单份原价+折扣价
                                                isFinish = true;//标记活动处理完成
                                            }
                                        }
                                    }
                                    break;
                                case 2://时段优惠
                                    orderCarDetailVO.setFinishPrice(payMoney.multiply(activity.getDiscount()));//应付金额 = 明细价格 * 优惠折扣
                            }
                        }
                    }
                }
            }
            log.info("活动处理完成");
            //计算订单最终总金额
            orderCarVO.setFinishPrice(BigDecimal.ZERO);
            for (OrderCarDetailVO orderCarDetailVO : orderCarVO.getOrderDetails()) {
                if (orderCarDetailVO.getFinishPrice() == null){orderCarDetailVO.setFinishPrice(orderCarDetailVO.getOriginalPrice());}//最终价格为null表示套餐没有优惠
                orderCarVO.setFinishPrice(orderCarVO.getFinishPrice().add(orderCarDetailVO.getFinishPrice()));
            }

            if (isLogin){//登录用户才能使用优惠券
                //开始处理优惠券
                List<Integer> unUseCouponIds = new ArrayList<>();//未核销的优惠券ID列表
                Map<Integer, MarketingCouponVO> couponMap = new HashMap<>();//获取所有优惠券,map<优惠券id,优惠券详细信息>
                if (orderCarVO.getCouponIds() != null){//使用了优惠券
                    unUseCouponIds = new ArrayList<>(orderCarVO.getCouponIds());//优惠券id列表
                    //判断用户是否有订单中的优惠券
                    List<ClientCoupon> clientCoupons = clientService.selectUserCouponByCId(loginUserId);//获取用户拥有的优惠券
                    for (Integer unUseCouponId : unUseCouponIds) {
                        boolean isFind = false;
                        for (ClientCoupon clientCoupon : clientCoupons) {
                            if (clientCoupon.getCouponId().equals(unUseCouponId)) {
                                isFind = true;
                                break;
                            }
                        }
                        if (!isFind) {throw new BusinessException("优惠券不存在");}
                    }
                    couponMap = orderCarVO.getCouponIds().stream().collect(Collectors.toMap(couponId->couponId,couponId->marketingService.selectCouponByCId(couponId),(oldValue,newValue)->newValue));
                    //遍历判断优惠券的合法性
                    for (MarketingCouponVO coupon : couponMap.values()) {
                        if (couponMap.size()>1 && !coupon.getOverlay()){throw new BusinessException("存在不可叠加的优惠券");}
                        //判断优惠券是否可用于该门店
                        if (!coupon.getAllDepts()){
                            List<Integer> deptIds = coupon.getDepts().stream().map(MarketingCDeptVO::getId).toList();
                            if (!deptIds.contains(orderCarVO.getDeptId())){throw new BusinessException("优惠券不可用于该门店");}
                        }
                    }
                    //先核销商品券和折扣券
                    for (OrderCarDetailVO orderCarDetailVO : orderCarVO.getOrderDetails()) {
                        if (orderCarDetailVO.getCouponId() != null){//该明细使用了优惠券
                            MarketingCouponVO coupon = couponMap.get(orderCarDetailVO.getCouponId());
                            if (coupon == null){throw new BusinessException("优惠券格式错误,请确保明细中的折扣券或商品券都存在优惠券列表中");}
                            //校验优惠券
                            boolean valid = false;
                            if (!coupon.getState()){throw new BusinessException("优惠券暂时不可用");}
                            if(LocalDateTime.now().isBefore(coupon.getEnableTime()) || LocalDateTime.now().isAfter(coupon.getDisableTime())){throw new BusinessException("当前不在优惠券有效期内");}
                            if(coupon.getType()!=1){//明细下的优惠券不允许为满减券
                                if(orderCarDetailVO.getGoodsType()==0){//明细为菜品
                                    for (MarketingCDishVO dish : coupon.getDishes()) {
                                        if(coupon.getAllDishes() || dish.getId().equals(orderCarDetailVO.getGoodsId())){
                                            valid = true;
                                            break;
                                        }
                                    }
                                } else if (orderCarDetailVO.getGoodsType()==1) {//明细为套餐
                                    for (MarketingCComboVO combo : coupon.getCombos()) {
                                        if(coupon.getAllCombos() || combo.getId().equals(orderCarDetailVO.getGoodsId())){
                                            valid = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!valid){throw new BusinessException("优惠券使用异常");}
                            //核销优惠券
                            unUseCouponIds.remove(orderCarDetailVO.getCouponId());//优惠券核销
                            BigDecimal payMoney = orderCarDetailVO.getFinishPrice();
                            if(coupon.getType() == 0){//折扣券
                                payMoney = payMoney.multiply(coupon.getDiscount());
                            } else if (coupon.getType() == 2) {//商品券
                                payMoney = payMoney.subtract(coupon.getDerate());
                            }
                            payMoney = payMoney.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : payMoney;
                            orderCarDetailVO.setFinishPrice(payMoney);
                        }
                    }
                }
                //计算订单总金额
                orderCarVO.setFinishPrice(BigDecimal.ZERO);
                for (OrderCarDetailVO orderCarDetailVO : orderCarVO.getOrderDetails()) {
                    if (orderCarDetailVO.getFinishPrice() == null){orderCarDetailVO.setFinishPrice(orderCarDetailVO.getOriginalPrice());}//最终价格为null表示套餐没有优惠
                    orderCarVO.setFinishPrice(orderCarVO.getFinishPrice().add(orderCarDetailVO.getFinishPrice()));
                }
                //核销满减券
                if (!unUseCouponIds.isEmpty()){//还有未核销的满减券
                    for (Integer couponId : unUseCouponIds) {
                        if (couponMap.get(couponId).getType()!=1){throw new BusinessException("优惠券格式错误,请确保折扣券和商品券都绑定了明细");}
                        if (orderCarVO.getFinishPrice().compareTo(couponMap.get(couponId).getThreshold())>0){//判断支付金额是否达到减免的门槛金额
                            //达到减免的门槛金额
                            BigDecimal amount = orderCarVO.getFinishPrice().subtract(couponMap.get(couponId).getDerate());
                            amount = amount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : amount;//订单价格不能小于0
                            orderCarVO.setFinishPrice(amount);
                        }
                    }
                }
                log.info("优惠券处理完成");
            }else {//未登录,直接计算总金额
                //计算订单总金额
                orderCarVO.setFinishPrice(BigDecimal.ZERO);
                for (OrderCarDetailVO orderCarDetailVO : orderCarVO.getOrderDetails()) {
                    if (orderCarDetailVO.getFinishPrice() == null){orderCarDetailVO.setFinishPrice(orderCarDetailVO.getOriginalPrice());}//最终价格为null表示套餐没有优惠
                    orderCarVO.setFinishPrice(orderCarVO.getFinishPrice().add(orderCarDetailVO.getFinishPrice()));
                }
            }
        }
        //存入redis
        String orderCarDTOJson = objectMapper.writeValueAsString(orderCarDTO);
        stringRedisTemplate.opsForValue().set("client:goods:car:user:"+loginUserId,orderCarDTOJson,7, TimeUnit.DAYS);
        return orderCarVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCarVO getOrderCar(Integer loginUserId) throws JsonProcessingException {
        String orderCarVOJson = stringRedisTemplate.opsForValue().get("client:goods:car:user:" + loginUserId);
        if (orderCarVOJson != null){//购物车不为空
            OrderCarDTO orderCarDTO = objectMapper.readValue(orderCarVOJson, OrderCarDTO.class);
            OrderCarVO orderCarVO = this.updateOrderCar(loginUserId, orderCarDTO);
            orderCarVO.setDeptName(deptService.selectDeptByDId(orderCarVO.getDeptId()).getName());
            if (orderCarVO.getActivityId() != null){
                orderCarVO.setActivityName(marketingService.selectActivityByAId(orderCarVO.getActivityId()).getName());
            }
            return orderCarVO;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer insertOrder(Integer loginUserId,OrderCouponDTO orderCouponDTO) throws JsonProcessingException {
        OrderExDTO orderExDTO;
        if (orderCouponDTO == null){//普通订单
            OrderCarVO orderCar = this.getOrderCar(loginUserId);
            if (orderCar == null){throw new BusinessException("购物车为空");}
            String orderCarJson = objectMapper.writeValueAsString(orderCar);
            orderExDTO = objectMapper.readValue(orderCarJson, OrderExDTO.class);
            //构建订单
            orderExDTO.setNo(uniqueNo.getUniqueNo("ORD"));//设置订单编号
            orderExDTO.setClientType(1);//设置客户类型为客户端用户
            orderExDTO.setClientId(loginUserId);//设置客户ID
            orderExDTO.setExpirationTime(LocalDateTime.now().plusMinutes(6));//设置订单支付超时时间
            orderExDTO.setState(0);//设置订单状态为待支付
            orderMapper.insertOrder(orderExDTO);//插入订单
            //给订单明细绑定订单id
            for (OrderExDetailDTO orderDetail : orderExDTO.getOrderDetails()) {
                orderDetail.setOrderId(orderExDTO.getId());
            }
            orderMapper.batchInsertOrderDetail(orderExDTO.getOrderDetails());//批量插入明细
            List<Integer> dishIds = new ArrayList<>();//订单中包含的所有菜品ID
            List<ConfigMaterial> dishConfigValues = new ArrayList<>();//订单中所有菜品的配置项值
            List<OrderExGoodsConfigDTO> orderExDetailConfigList = new ArrayList<>();//收集菜品明细中的配置项
            List<OrderExComboDetailDTO> comboDetailList = new ArrayList<>();//收集套餐明细中的菜品
            for (OrderExDetailDTO orderDetail : orderExDTO.getOrderDetails()) {
                if (orderDetail.getGoodsType() == 0){//该条订单明细是菜品
                    dishIds.add(orderDetail.getGoodsId());//收集id到菜品id集合
                    //收集菜品明细中的配置项
                    for (OrderExGoodsConfigDTO config : orderDetail.getConfigs()) {
                        config.setDetailId(orderDetail.getId());
                        config.setDishId(orderDetail.getGoodsId());
                        orderExDetailConfigList.add(config);
                        dishConfigValues.add(new ConfigMaterial(config.getDishId(),null,config.getValueId(),null,null,null,null));//收集菜品配置项
                    }
                } else if(orderDetail.getGoodsType() == 1){//该条订单明细是套餐
                    //收集套餐明细中的菜品
                    for (OrderExComboDetailDTO comboDetail : orderDetail.getComboDetails()) {
                        dishIds.add(comboDetail.getDishId());//收集id到菜品id集合
                        comboDetail.setDetailId(orderDetail.getId());
                        comboDetailList.add(comboDetail);
                    }
                }
            }
            if (!orderExDetailConfigList.isEmpty()){
                orderMapper.batchInsertODConfig(orderExDetailConfigList);//批量插入明细的菜品配置
            }
            if (!comboDetailList.isEmpty()){
                orderMapper.batchInsertOCDetail(comboDetailList);//批量插入套餐明细
            }
            List<OrderExCDConfigDTO> orderExCDConfigList = new ArrayList<>();//套餐明细中所有菜品的配置项
            for (OrderExDetailDTO orderDetail : orderExDTO.getOrderDetails()) {
                if(orderDetail.getGoodsType() == 1){//该条订单明细是套餐
                    //收集套餐明细中所有菜品的配置项
                    for (OrderExComboDetailDTO comboDetail : orderDetail.getComboDetails()) {
                        for (OrderExCDConfigDTO config : comboDetail.getConfigs()) {
                            config.setOrderComboDetailId(comboDetail.getId());
                            config.setDishId(comboDetail.getDishId());
                            orderExCDConfigList.add(config);
                            dishConfigValues.add(new ConfigMaterial(config.getDishId(),null,config.getValueId(),null,null,null,null));//收集菜品配置项
                        }
                    }
                }
            }
            if (!orderExCDConfigList.isEmpty()){
                orderMapper.batchInsertOCDConfig(orderExCDConfigList);//批量插入套餐明细的配置
            }

            //获取订单中包含的所有菜品的基础配方
            List<Formula> dishBaseFormulas;
            //配方Map集合,map<菜品id,map<原料id,原料用量>>
            Map<Integer,Map<Integer,Integer>> formulaMap = new HashMap<>();
            if (!dishIds.isEmpty()){
                dishIds = dishIds.stream().distinct().toList();//去重
                dishBaseFormulas = menuService.selectFormulaByDIds(dishIds);//获取菜品基础配方
                //配方Map集合,map<菜品id,map<原料id,原料用量>>
                formulaMap = dishBaseFormulas.stream().collect(Collectors.groupingBy(Formula::getDishId,Collectors.toMap(Formula::getMaterialId,Formula::getNum)));
            }
            //获取订单中包含的所有菜品配置值的原料用量差值
            //原料差值集合,map<菜品id,map<配置值id,map<原料id,原料差值>>>
            Map<Integer,Map<Integer,Map<Integer,Integer>>> dishMaterialDiffMap = new HashMap<>();
            if (!dishConfigValues.isEmpty()){
                dishConfigValues = dishConfigValues.stream().distinct().toList();
                dishConfigValues = menuService.selectMaterialDifference(dishConfigValues);//获取原料差值
                dishMaterialDiffMap = dishConfigValues.stream().collect(Collectors.groupingBy(ConfigMaterial::getDishId,Collectors.groupingBy(ConfigMaterial::getValueId,Collectors.toMap(ConfigMaterial::getMaterialId,ConfigMaterial::getSpread))));
            }
            //计算订单中包含的所有菜品的原料用量
            Map<Integer,Integer> MaterialUsage = new HashMap<>();//map<原料id,原料用量>
            for (OrderExDetailDTO orderDetail : orderExDTO.getOrderDetails()) {
                if (orderDetail.getGoodsType() == 0){//该条订单明细是菜品
                    for (Map.Entry<Integer,Integer> entry:formulaMap.get(orderDetail.getGoodsId()).entrySet()){
                        MaterialUsage.merge(entry.getKey(),entry.getValue()*orderDetail.getNum(),Integer::sum);
                    }
                    for (OrderExGoodsConfigDTO config : orderDetail.getConfigs()) {
                        for (Map.Entry<Integer,Integer> entry:dishMaterialDiffMap.get(orderDetail.getGoodsId()).get(config.getValueId()).entrySet()){
                            MaterialUsage.merge(entry.getKey(),entry.getValue()*orderDetail.getNum(),Integer::sum);
                        }
                    }
                }else if (orderDetail.getGoodsType() == 1){
                    for (OrderExComboDetailDTO comboDetail : orderDetail.getComboDetails()) {
                        for (Map.Entry<Integer,Integer> entry:formulaMap.get(comboDetail.getDishId()).entrySet()){
                            MaterialUsage.merge(entry.getKey(),entry.getValue()*comboDetail.getNum()*orderDetail.getNum(),Integer::sum);
                        }
                        for (OrderExCDConfigDTO config : comboDetail.getConfigs()) {
                            for (Map.Entry<Integer,Integer> entry:dishMaterialDiffMap.get(comboDetail.getDishId()).get(config.getValueId()).entrySet()){
                                MaterialUsage.merge(entry.getKey(),entry.getValue()*comboDetail.getNum()*orderDetail.getNum(),Integer::sum);
                            }
                        }
                    }
                }
            }
            //扣减原材料库存
            //获取门店对应仓库id
            List<Integer> storeIds = inventoryService.selectStoreIdsByDeptId(orderExDTO.getDeptId());
            for (Map.Entry<Integer,Integer> entry:MaterialUsage.entrySet()){
                inventoryExService.updateRepertory(2,orderExDTO.getId(),storeIds.get(0),entry.getKey(),1,entry.getValue());
            }

            if (!orderExDTO.getCouponIds().isEmpty()){//订单使用了优惠券
                orderMapper.batchInsertOCoupon(orderExDTO.getId(), orderExDTO.getCouponIds());//批量插入订单使用的优惠券
                List<MarketingCUseNum> couponUseNumList = new ArrayList<>();
                Map<Integer,Integer> couponUseNumMap = new HashMap<>();
                for (Integer couponId : orderExDTO.getCouponIds()) {
                    orderMapper.deleteClientCoupon(loginUserId, couponId);//扣除用户拥有的对应优惠券
                    //统计订单中每种优惠券核销数量
                    couponUseNumMap.put(couponId, couponUseNumMap.getOrDefault(couponId, 0) + 1);
                }
                for (Map.Entry<Integer, Integer> entry : couponUseNumMap.entrySet()){
                    MarketingCUseNum marketingCUseNum = new MarketingCUseNum(entry.getKey(), entry.getValue());
                    couponUseNumList.add(marketingCUseNum);
                }
                marketingService.updateCouponUseNum(couponUseNumList);//修改优惠券核销数量
            }
            //清空购物车
            stringRedisTemplate.delete("sys:goods:car:user:" + loginUserId);


        }else {//优惠券订单
            Coupon coupon = marketingService.selectCouponBasicByCId(orderCouponDTO.getCouponId());//获取优惠券信息
            if (coupon == null){throw new BusinessException("优惠券不存在");}
            Integer limit = coupon.getLimit();
            if(limit != null && limit > 0){//优惠券有数量限制
                Integer hasNum = clientService.selectUserCouponNumByCId(loginUserId, orderCouponDTO.getCouponId());//获取用户拥有该优惠券数量
                if(hasNum + orderCouponDTO.getNum() > limit){throw new BusinessException("该优惠券已达持有上限,请先使用已有优惠券");}
            }
            Integer updateRow = marketingService.updateCouponInvByCId(orderCouponDTO.getCouponId(), orderCouponDTO.getNum()*-1);//扣减优惠券库存
            if (updateRow <= 0){throw new BusinessException("优惠券已售罄");}
            OrderExDetailDTO orderExDetailDTO = new OrderExDetailDTO(null, null, 2, orderCouponDTO.getCouponId(), null, null, orderCouponDTO.getNum(), null, coupon.getPrice().multiply(BigDecimal.valueOf(orderCouponDTO.getNum())));
            orderExDTO = new OrderExDTO();
            orderExDTO.setNo(uniqueNo.getUniqueNo("ORD"));//设置订单编号
            orderExDTO.setType(1);
            orderExDTO.setClientType(1);//设置客户类型为客户端用户
            orderExDTO.setClientId(loginUserId);//设置客户ID
            orderExDTO.setExpirationTime(LocalDateTime.now().plusMinutes(6));//设置订单支付超时时间
            orderExDTO.setState(0);//设置订单状态为待支付
            orderExDTO.setFinishPrice(orderExDetailDTO.getFinishPrice());//设置订单总价格
            orderMapper.insertOrder(orderExDTO);//插入订单
            orderExDetailDTO.setOrderId(orderExDTO.getId());
            orderExDTO.setOrderDetails(List.of(orderExDetailDTO));//设置订单明细
            orderMapper.batchInsertOrderDetail(orderExDTO.getOrderDetails());//批量插入明细
        }
        return orderExDTO.getId();
    }

    @Override
    public PageResult<OrderSimpleVO> selectOrderList(Integer loginUserId, Integer page, Integer pageSize, String no, Integer type, String dept, Integer state, Integer eatMode) {
        Integer total = orderMapper.countClientOrderList(loginUserId,no,type,dept,state,eatMode);
        Integer skip = (page-1)*pageSize;
        //获取订单基本信息
        List<OrderSimpleVO> orderBaseList = orderMapper.selectClientOrderList(loginUserId,skip,pageSize,no,type,dept,state,eatMode);
        List<Integer> orderIds = orderBaseList.stream().map(OrderSimpleVO::getId).toList();
        if (CollectionUtils.isEmpty(orderIds)) {
            return new PageResult<>(total, orderBaseList);
        }
        List<OrderTDetailDTO> orderTDetails = orderMapper.batchSelectOrderDetailByOId(orderIds);//批量获取订单明细
        List<Integer> dishDetailIds = orderTDetails.stream()
                .filter(e->e.getGoodsType()==0)//过滤出菜品的明细
                .map(OrderTDetailDTO::getDetailId).toList();//收集菜品的明细id
        List<Integer> comboDetailIds = orderTDetails.stream()
                .filter(e->e.getGoodsType()==1)//过滤出套餐的明细
                .map(OrderTDetailDTO::getDetailId).toList();//收集套餐的明细id
        List<Integer> couponIds = orderTDetails.stream()
                .filter(e->e.getGoodsType()==2)//过滤出优惠券的明细
                .map(OrderTDetailDTO::getGoodsId).toList();//收集优惠券的id

        List<MarketingCSimDTO> marketingCSims = !couponIds.isEmpty() ? marketingService.selectCouponSimpleByCIds(couponIds) : new ArrayList<>();
        List<OrderTDConfigDTO> oDConfigs =!dishDetailIds.isEmpty() ? orderMapper.selectODConfigByDIds(dishDetailIds) : new ArrayList<>();
        List<OrderTCDetailDTO> oDCDetail =!comboDetailIds.isEmpty() ? orderMapper.selectOCDetailByDIds(comboDetailIds) : new ArrayList<>();
        List<Integer> comboInnDetailIds=!comboDetailIds.isEmpty() ? oDCDetail.stream().map(OrderTCDetailDTO::getId).toList() : new ArrayList<>();
        //根据套餐中的唯一id列表获取其菜品信息
        List<OrderTCDConfigDTO> oCDConfigs =!comboDetailIds.isEmpty() ? orderMapper.selectOCDConfigByDIds(comboInnDetailIds) : new ArrayList<>();

        Map<Integer,MarketingCSimDTO> couponNameMap = marketingCSims.stream()
                .collect(Collectors.toMap(MarketingCSimDTO::getId, item->item));
        Map<Integer, List<OrderTDetailDTO>> detailOrderMap = orderTDetails.stream()
                .collect(Collectors.groupingBy(OrderTDetailDTO::getOrderId));
        Map<Integer, List<OrderTDConfigDTO>> dishConfigMap = oDConfigs.stream()
                .collect(Collectors.groupingBy(OrderTDConfigDTO::getDetailId));
        Map<Integer, List<OrderTCDetailDTO>> comboSubMap = oDCDetail.stream()
                .collect(Collectors.groupingBy(OrderTCDetailDTO::getDetailID));
        Map<Integer, List<OrderTCDConfigDTO>> comboConfigMap = oCDConfigs.stream()
                .collect(Collectors.groupingBy(OrderTCDConfigDTO::getComboDetailId));

        //组装VO返回数据
        for (OrderSimpleVO order : orderBaseList) {
            List<OrderTDetailDTO> details = detailOrderMap.getOrDefault(order.getId(), Collections.emptyList());
            List<OrderDetailVO> detailVOList = new ArrayList<>();
            for (OrderTDetailDTO detail : details) {
                OrderDetailVO detailVO = new OrderDetailVO();
                BeanUtils.copyProperties(detail, detailVO);
                detailVO.setId(detail.getDetailId());
                if (detail.getGoodsType() == 2){
                    detailVO.setGoodsName(couponNameMap.get(detail.getGoodsId()).getName());
                    detailVO.setCover(couponNameMap.get(detail.getGoodsId()).getCover());
                }
                if (detail.getGoodsType() == 0) {
                    List<OrderGoodsConfigVO> configVOs = dishConfigMap
                            .getOrDefault(detail.getDetailId(), Collections.emptyList())
                            .stream()
                            .map(cfg -> {
                                OrderGoodsConfigVO vo = new OrderGoodsConfigVO();
                                vo.setName(cfg.getName());
                                vo.setValueId(cfg.getValueId());
                                return vo;
                            }).toList();
                    detailVO.setConfigs(configVOs);
                } else if (detail.getGoodsType() == 1) {
                    List<OrderComboDetailVO> comboVOs = comboSubMap
                            .getOrDefault(detail.getDetailId(), Collections.emptyList())
                            .stream()
                            .map(combo -> {
                                OrderComboDetailVO comboVO = new OrderComboDetailVO();
                                comboVO.setDishId(combo.getDishId());
                                comboVO.setDishName(combo.getDishName());
                                comboVO.setNum(combo.getNum());
                                // 套餐子项配置
                                List<OrderGoodsConfigVO> subConfigs = comboConfigMap
                                        .getOrDefault(combo.getId(), Collections.emptyList())
                                        .stream()
                                        .map(cfg -> {
                                            OrderGoodsConfigVO vo = new OrderGoodsConfigVO();
                                            vo.setName(cfg.getName());
                                            vo.setValueId(cfg.getValueId());
                                            return vo;
                                        }).toList();
                                comboVO.setConfigs(subConfigs);
                                return comboVO;
                            }).toList();
                    detailVO.setComboDetails(comboVOs);
                }
                detailVOList.add(detailVO);
            }
            order.setOrderDetails(detailVOList);
        }
        return new PageResult<>(total,orderBaseList);
    }

    @Override
    public OrderVO selectOrderById(Integer loginUserId, Integer id) {
        //获取订单基本信息
        OrderVO orderVO = orderMapper.selectOrderById(id);
        if (orderVO == null){throw new BusinessException("订单不存在");}
        if (!Objects.equals(orderVO.getClientId(), loginUserId)){throw new AuthorityException("权限不足");}
        //获取订单明细
        orderVO.setOrderDetails(orderMapper.selectOrderDetailByOId(orderVO.getId()));
        //获取订单使用的优惠券
        orderVO.setCoupons(orderMapper.selectOrderCouponByOId(orderVO.getId()));
        List<Integer> dishDetailIds = orderVO.getOrderDetails().stream()
                .filter(e->e.getGoodsType()==0)//过滤出菜品的明细
                .map(OrderDetailVO::getId).toList();//收集菜品的明细id
        List<Integer> comboDetailIds = orderVO.getOrderDetails().stream()
                .filter(e->e.getGoodsType()==1)//过滤出套餐的明细
                .map(OrderDetailVO::getId).toList();//收集套餐的明细id
        List<Integer> couponIds = orderVO.getOrderDetails().stream()
                .filter(e->e.getGoodsType()==2)//过滤出优惠券的明细
                .map(OrderDetailVO::getGoodsId).toList();//收集优惠券的id

        List<MarketingCSimDTO> couponList =!couponIds.isEmpty()? marketingService.selectCouponSimpleByCIds(couponIds):Collections.emptyList();
        //根据菜品明细id列表获取其配置信息
        List<OrderTDConfigDTO> dishConfigList =!dishDetailIds.isEmpty()? orderMapper.selectODConfigByDIds(dishDetailIds):Collections.emptyList();
        //根据套餐明细id列表获取其菜品信息
        List<OrderTCDetailDTO> comboInnerDishList =!comboDetailIds.isEmpty()? orderMapper.selectOCDetailByDIds(comboDetailIds):Collections.emptyList();
        //收集套餐明细中的唯一id列表
        List<Integer> comboInnDetailIds = comboInnerDishList.stream().map(OrderTCDetailDTO::getId).toList();
        //根据套餐中的唯一id列表获取其菜品信息
        List<OrderTCDConfigDTO> comboConfigList =!comboInnDetailIds.isEmpty()? orderMapper.selectOCDConfigByDIds(comboInnDetailIds):Collections.emptyList();

        // key:明细id value:菜品配置集合
        Map<Integer, List<OrderTDConfigDTO>> dishConfigMap = dishConfigList.stream()
                .collect(Collectors.groupingBy(OrderTDConfigDTO::getDetailId));
        // key:套餐明细id value:套餐内嵌菜品集合
        Map<Integer, List<OrderTCDetailDTO>> comboDishMap = comboInnerDishList.stream()
                .collect(Collectors.groupingBy(OrderTCDetailDTO::getDetailID));
        // key:套餐内嵌菜品id value:套餐配置集合
        Map<Integer, List<OrderTCDConfigDTO>> comboItemConfigMap = comboConfigList.stream()
                .collect(Collectors.groupingBy(OrderTCDConfigDTO::getComboDetailId));
        Map<Integer,MarketingCSimDTO> couponMap = couponList.stream()
                .collect(Collectors.toMap(MarketingCSimDTO::getId, item->item));

        for (OrderDetailVO orderDetail : orderVO.getOrderDetails()) {
            if (orderDetail.getGoodsType() == 0){//明细类型是菜品
                List<OrderGoodsConfigVO> dishConfigs = dishConfigMap.getOrDefault(orderDetail.getId(), Collections.emptyList())
                        .stream()
                        .map(dto->{
                            OrderGoodsConfigVO vo = new OrderGoodsConfigVO();
                            vo.setValueId(dto.getValueId());
                            vo.setName(dto.getName());
                            return vo;
                        })
                        .toList();
                orderDetail.setConfigs(dishConfigs);
            }else if (orderDetail.getGoodsType() == 1){//明细类型是套餐
                // 套餐内嵌菜品封装
                List<OrderComboDetailVO> comboDetailVOS = comboDishMap.getOrDefault(orderDetail.getId(), Collections.emptyList())
                        .stream()
                        .map(item -> {
                            OrderComboDetailVO orderComboDetailVO = new OrderComboDetailVO();
                            orderComboDetailVO.setDishId(item.getDishId());
                            orderComboDetailVO.setDishName(item.getDishName());
                            orderComboDetailVO.setNum(item.getNum());
                            // 套餐内菜品配置
                            List<OrderGoodsConfigVO> itemConfigs = comboItemConfigMap.getOrDefault(item.getId(), Collections.emptyList())
                                    .stream()
                                    .map(dto->{
                                        OrderGoodsConfigVO orderGoodsConfigVO = new OrderGoodsConfigVO();
                                        orderGoodsConfigVO.setValueId(dto.getValueId());
                                        orderGoodsConfigVO.setName(dto.getName());
                                        return orderGoodsConfigVO;
                                    })
                                    .toList();
                            orderComboDetailVO.setConfigs(itemConfigs);
                            return orderComboDetailVO;
                        }).toList();
                orderDetail.setComboDetails(comboDetailVOS);
            } else if (orderDetail.getGoodsType() == 2) {//明细类型是优惠券
                orderDetail.setGoodsName(couponMap.get(orderDetail.getGoodsId()).getName());
                orderDetail.setCover(couponMap.get(orderDetail.getGoodsId()).getCover());
            }
        }
        return orderVO;
    }

    @Override
    public OrderPayVO payOrder(OrderPayDTO orderPayDTO) {
        Order order = orderMapper.getBaseOrderByOId(orderPayDTO.getOrderId());//获取订单
        if (order.getState() != 0){throw new BusinessException("订单状态异常");}
        if (LocalDateTime.now().isAfter(order.getExpirationTime())){throw new BusinessException("订单已超时");}
        //TODO编写支付相关逻辑

        //TODO测试代码,测试支付成功,后续移到支付结果回调接口
        switch (order.getType()){
            case 0://订单类型是点餐
                order.setState(2);//设置订单状态为制作中

                break;
            case 1://订单类型是优惠券
                order.setState(4);//设置订单状态为已完成
                List<OrderDetail> orderDetailList = orderMapper.selectOrderBaseDetailByOIds(List.of(order.getId()));//获取订单明细
                for (OrderDetail orderDetail : orderDetailList) {
                    //添加优惠券到用户券包
                    marketingService.insertCouponToClient(orderDetail.getGoodsId(), order.getClientId(), orderDetail.getNum());//添加优惠券到用户券包
                }
                break;
        }
        order.setPayTime(LocalDateTime.now());//设置订单支付时间
        orderMapper.updateOrder(order);//修改订单
        return null;
    }

    @Override
    public Integer selectOrderState(Integer loginUserId,Integer id) {
        OrderVO orderVO = orderMapper.selectOrderById(id);
        if (orderVO == null){throw new BusinessException("订单不存在");}
        if (!Objects.equals(orderVO.getClientId(), loginUserId)){throw new AuthorityException("权限不足");}
        return orderVO.getState();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderState(Integer loginUserId, OrderStateDTO orderStateDTO) {
        Order order = orderMapper.getBaseOrderByOId(orderStateDTO.getId());//获取订单
        if (order == null){throw new BusinessException("订单不存在");}
        if (order.getClientType() == 0 || !Objects.equals(order.getClientId(), loginUserId)){throw new AuthorityException("权限不足");}
        switch (orderStateDTO.getState()){
            case 1://取消订单
                if (order.getState() != 0){throw new BusinessException("订单已支付");}
                switch (order.getType()){
                    case 0://订单类型是点餐
                        order.setPickupNum(null);//清空取餐号
                        //原料库存回滚
                        inventoryExService.rollbackRepertory(2,List.of(order.getId()));
                        //优惠券回滚
                        orderService.rollbackOrderCoupon(List.of(order.getId()));
                    case 1://订单类型是优惠券
                        //优惠券回滚到库存中
                        List<OrderDetail> orderDetailList = orderMapper.selectOrderBaseDetailByOIds(List.of(order.getId()));//获取订单明细
                        for (OrderDetail orderDetail : orderDetailList) {
                            marketingService.updateCouponInvByCId(orderDetail.getGoodsId(), orderDetail.getNum());//添加优惠券库存
                        }
                }
                order.setState(1);//设置订单状态为取消
                orderMapper.updateOrder(order);//修改订单
                break;
            case 3://订单制作完成
                if (order.getState() != 2){throw new BusinessException("订单状态异常");}
                order.setState(3);//设置订单状态为待取餐
                order.setCompleteTime(LocalDateTime.now());//设置制作完成时间
                orderMapper.updateOrder(order);//修改订单
                break;
            case 4://订单已取餐
                if (order.getState() != 3){throw new BusinessException("订单状态异常");}
                order.setState(4);//设置订单状态为已完成
                order.setTackTime(LocalDateTime.now());//设置取餐时间
                order.setPickupNum(null);//清空取餐号
                orderMapper.updateOrder(order);//修改订单
                break;
            case 5://申请退款
                if (order.getState() == 0 || order.getState() == 1){throw new BusinessException("订单未支付");}
                if (orderStateDTO.getCause()==null){throw new BusinessException("请填写申请退款原因");}
                if (order.getType() == 1){//订单类型是优惠券
                    //判断用户的背包中是否包含足够数量的订单中的优惠券
//                    Map<Integer,Integer> couponCountMap = new HashMap<>();//存储订单中优惠券Id和数量
                    List<OrderDetail> orderDetailList = orderMapper.selectOrderBaseDetailByOIds(List.of(order.getId()));//获取订单明细
                    for (OrderDetail orderDetail : orderDetailList) {
                        for (int i = 0; i < orderDetail.getNum(); i++) {
                            //扣除用户拥有的对应优惠券
                            Integer deleteCount = orderMapper.deleteClientCoupon(loginUserId, orderDetail.getGoodsId());
                            if (deleteCount == 0){throw new BusinessException("该订单包含的优惠券已被使用");}
                        }
                    }
                    //优惠券满足退款条件后可自动退款
                    orderService.refundOrder(loginUserId,0,new OrderRefundDTO(order.getId(),false));
                }
                order.setState(5);
                orderMapper.updateOrder(order);//修改订单
                break;
            default:
                throw new BusinessException("没有该订单状态");
        }
    }
}
