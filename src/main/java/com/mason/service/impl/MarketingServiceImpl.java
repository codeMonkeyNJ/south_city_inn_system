package com.mason.service.impl;

import com.mason.domain.PageResult;
import com.mason.domain.dto.*;
import com.mason.domain.po.*;
import com.mason.domain.vo.*;
import com.mason.exception.BusinessException;
import com.mason.mapper.MarketingMapper;
import com.mason.service.MarketingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MarketingServiceImpl implements MarketingService {
    @Autowired
    private MarketingMapper marketingMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCoupon(MarketingCouponDTO marketingCouponDTO) {
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(marketingCouponDTO, coupon);
        Integer couponType = marketingCouponDTO.getType();
        if (couponType == 0 && marketingCouponDTO.getDiscount()==null){throw new BusinessException("请填写优惠券折扣");}
        if ((couponType == 1 || couponType == 2) && marketingCouponDTO.getDerate()==null){throw new BusinessException("请填写优惠券折价");}
        coupon.setStock(marketingCouponDTO.getSum());
        marketingMapper.insertCoupon(coupon);//插入优惠券
        if (!marketingCouponDTO.getAllCombos() && !marketingCouponDTO.getCombos().isEmpty()){
            marketingMapper.insertCouponCombo(coupon.getId(), marketingCouponDTO.getCombos());//插入优惠券关联的套餐
        }
        if (!marketingCouponDTO.getAllDishes() && !marketingCouponDTO.getDishes().isEmpty()){
            marketingMapper.insertCouponDish(coupon.getId(), marketingCouponDTO.getDishes());//插入优惠券关联的菜品
        }
        if (!marketingCouponDTO.getAllDepts() && !marketingCouponDTO.getDepts().isEmpty()){
            marketingMapper.insertCouponDept(coupon.getId(), marketingCouponDTO.getDepts());//插入优惠券关联的门店
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCoupon(MarketingCouponDTO marketingCouponDTO) {
        Coupon coupon = marketingMapper.selectCouponByCId(marketingCouponDTO.getId());
        if (coupon == null){throw new BusinessException("优惠券不存在");}
        if (coupon.getState()){throw new BusinessException("不允许修改已启用的优惠券");}
        if (marketingCouponDTO.getSum()<coupon.getUsage()){throw new BusinessException("优惠券总量不能小于已使用数量");}
        BeanUtils.copyProperties(marketingCouponDTO, coupon);
        coupon.setStock(marketingCouponDTO.getSum() - coupon.getUsage());
        Integer couponType = marketingCouponDTO.getType();
        if (couponType == 0 && marketingCouponDTO.getDiscount()==null){throw new BusinessException("请填写优惠券折扣");}
        if ((couponType == 1 || couponType == 2) && marketingCouponDTO.getDerate()==null){throw new BusinessException("请填写优惠券折价");}
        marketingMapper.updateCoupon(coupon);//修改优惠券基本信息
        marketingMapper.deleteCouponCombo(coupon.getId());//删除优惠券原来关联的套餐
        if (!marketingCouponDTO.getAllCombos() && !marketingCouponDTO.getCombos().isEmpty()){
            marketingMapper.insertCouponCombo(coupon.getId(), marketingCouponDTO.getCombos());//插入优惠券关联的套餐
        }
        marketingMapper.deleteCouponDish(coupon.getId()); //删除优惠券原来关联的菜品
        if (!marketingCouponDTO.getAllDishes() && !marketingCouponDTO.getDishes().isEmpty()){
            marketingMapper.insertCouponDish(coupon.getId(), marketingCouponDTO.getDishes());//插入优惠券关联的菜品
        }
        marketingMapper.deleteCouponDept(coupon.getId());//删除优惠券原来关联的门店
        if (!marketingCouponDTO.getAllDepts() && !marketingCouponDTO.getDepts().isEmpty()){
            marketingMapper.insertCouponDept(coupon.getId(), marketingCouponDTO.getDepts());//插入优惠券关联的门店
        }
    }

    @Override
    public void updateCouponStateByCId(Integer id, Boolean state) {
        marketingMapper.updateCouponStateByCId(id, state);
    }

    @Override
    public PageResult<MarketingCSimpleVO> selectCouponList(Integer page, Integer pageSize, String name, Integer type, Boolean state) {
        Integer total = marketingMapper.countCouponList(name, type, state);
        Integer skip = pageSize * (page - 1);
        List<MarketingCSimpleVO> items = marketingMapper.selectCouponList(skip, pageSize, name, type, state);
        return new PageResult<>(total, items);
    }

    @Override
    public MarketingCouponVO selectCouponByCId(Integer id) {
        MarketingCouponVO marketingCouponVO = new MarketingCouponVO();
        Coupon coupon = marketingMapper.selectCouponByCId(id);
        if (coupon == null){throw  new BusinessException("优惠券不存在");}
        BeanUtils.copyProperties(coupon, marketingCouponVO);
        marketingCouponVO.setDepts(marketingMapper.selectCouponDeptByCId(id));//获取优惠券关联的门店
        marketingCouponVO.setDishes(marketingMapper.selectCouponDishByCId(id));//获取优惠券关联的菜品
        marketingCouponVO.setCombos(marketingMapper.selectCouponComboByCId(id));//获取优惠券关联的套餐
        return marketingCouponVO;
    }

    @Override
    public Coupon selectCouponBasicByCId(Integer id) {
        return marketingMapper.selectCouponPriceByCId(id);
    }

    @Override
    public List<MarketingCSimDTO> selectCouponSimpleByCIds(List<Integer> ids) {
        return marketingMapper.selectCouponSimpleByCIds(ids);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteCoupon(List<Integer> ids) {
        List<Coupon> coupons = marketingMapper.selectCouponByCIds(ids);
        for (Coupon coupon : coupons) {
            //检查所有优惠券是否符合删除标准
            if (coupon.getState()) {
                throw new BusinessException("不允许删除已启用的优惠券");
            }
            LocalDateTime now = LocalDateTime.now();
            log.info(String.valueOf(now.isBefore(coupon.getDisableTime())));
            log.info(String.valueOf(now.isAfter(coupon.getEnableTime())));
            //判断优惠券是否在启用时间段内且未核销的优惠券
            if ((now.isBefore(coupon.getDisableTime()) && now.isAfter(coupon.getEnableTime()))
                    && ((coupon.getUsage() + coupon.getStock()) < coupon.getSum())) {
                throw new BusinessException("删除失败,存在未核销的优惠券");
            }
        }
        marketingMapper.batchDeleteCoupon(ids);//批量删除优惠券
        marketingMapper.batchDeleteCouponDept(ids);//批量删除优惠券关联的部门
        marketingMapper.batchDeleteCouponDish(ids);//批量删除优惠券关联的菜品
        marketingMapper.batchDeleteCouponCombo(ids);//批量删除优惠券关联的套餐
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertActivity(MarketingActivityDTO marketingActivityDTO) {
        if(marketingActivityDTO.getRouteType() == 0 && marketingActivityDTO.getGoodsId() == null){throw new BusinessException("请选择跳转的商品");}
        if (marketingActivityDTO.getActivityType() == 0 && (marketingActivityDTO.getBuyNum() == null || marketingActivityDTO.getGiveNum() == null)){throw new BusinessException("请填写完整买送数量");}
        if (marketingActivityDTO.getActivityType() == 1 && (marketingActivityDTO.getBuyNum() == null || marketingActivityDTO.getDiscount() == null)){throw new BusinessException("请填写完整购买数量和折扣");}
        if (marketingActivityDTO.getActivityType() == 2 && (marketingActivityDTO.getStartTime() == null || marketingActivityDTO.getEndTime() == null)){throw new BusinessException("请填写完整开始时间和结束时间");}
        if(marketingActivityDTO.getAwardGoodsId() != null && marketingActivityDTO.getAwardGoodsType() == null){throw new BusinessException("请填写赠送商品类型");}
        Activity activity = new Activity();
        BeanUtils.copyProperties(marketingActivityDTO, activity);
        activity.setType(marketingActivityDTO.getRouteType());
        ActivityRule activityRule = new ActivityRule();
        BeanUtils.copyProperties(marketingActivityDTO, activityRule);
        activityRule.setGoodsType(marketingActivityDTO.getAwardGoodsType());
        activityRule.setGoodsId(marketingActivityDTO.getAwardGoodsId());
        activityRule.setType(marketingActivityDTO.getActivityType());
        marketingMapper.insertActivityRule(activityRule);
        activity.setRuleId(activityRule.getId());
        marketingMapper.insertActivity(activity);
        if (!activityRule.getAllDepts() && !marketingActivityDTO.getDepts().isEmpty()){
            marketingMapper.insertActivityDept(activityRule.getId(), marketingActivityDTO.getDepts());//插入活动关联的门店
        }
        if (!activityRule.getAllCombos() && !marketingActivityDTO.getCombos().isEmpty()){
            marketingMapper.insertActivityCombo(activityRule.getId(), marketingActivityDTO.getCombos());//插入活动关联的套餐
        }
        if (!activityRule.getAllDishes() && !marketingActivityDTO.getDishes().isEmpty()){
            marketingMapper.insertActivityDish(activityRule.getId(), marketingActivityDTO.getDishes());//插入活动关联的菜品
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(MarketingActivityDTO marketingActivityDTO) {
        if(marketingActivityDTO.getRouteType() == 0 && marketingActivityDTO.getGoodsId() == null){throw new BusinessException("请选择跳转的商品");}
        if (marketingActivityDTO.getActivityType() == 0 && (marketingActivityDTO.getBuyNum() == null || marketingActivityDTO.getGiveNum() == null)){throw new BusinessException("请填写完整买送数量");}
        if (marketingActivityDTO.getActivityType() == 1 && (marketingActivityDTO.getBuyNum() == null || marketingActivityDTO.getDiscount() == null)){throw new BusinessException("请填写完整购买数量和折扣");}
        if (marketingActivityDTO.getActivityType() == 2 && (marketingActivityDTO.getStartTime() == null || marketingActivityDTO.getEndTime() == null)){throw new BusinessException("请填写完整开始时间和结束时间");}
        if(marketingActivityDTO.getAwardGoodsId() != null && marketingActivityDTO.getAwardGoodsType() == null){throw new BusinessException("请填写赠送商品类型");}
        Activity activity = marketingMapper.selectActivityByAId(marketingActivityDTO.getId());//根据活动id获取活动信息
        BeanUtils.copyProperties(marketingActivityDTO, activity);
        activity.setType(marketingActivityDTO.getRouteType());
        ActivityRule activityRule = marketingMapper.selectActivityRuleByRId(activity.getRuleId());//根据活动规则id获取活动规则
        BeanUtils.copyProperties(marketingActivityDTO, activityRule);
        activityRule.setId(activity.getRuleId());
        activityRule.setGoodsId(marketingActivityDTO.getAwardGoodsId());
        activityRule.setGoodsType(marketingActivityDTO.getAwardGoodsType());
        activityRule.setType(marketingActivityDTO.getActivityType());
        Integer updateCount = marketingMapper.updateActivityRule(activityRule);//更新活动规则
        if (updateCount == 0){throw new BusinessException("活动不存在");}
        marketingMapper.updateActivity(activity);//更新活动
        marketingMapper.deleteActivityDept(activityRule.getId());//删除活动原来关联的部门
        if (!activityRule.getAllDepts() && !marketingActivityDTO.getDepts().isEmpty()){
            marketingMapper.insertActivityDept(activityRule.getId(), marketingActivityDTO.getDepts());//重新插入活动关联的门店
        }
        marketingMapper.deleteActivityDish(activityRule.getId());//删除活动原来关联的菜品
        if (!activityRule.getAllCombos() && !marketingActivityDTO.getCombos().isEmpty()){
            marketingMapper.insertActivityCombo(activityRule.getId(), marketingActivityDTO.getCombos());//重新插入活动关联的套餐
        }
        marketingMapper.deleteActivityCombo(activityRule.getId());//删除活动原来关联的套餐
        if (!activityRule.getAllDishes() && !marketingActivityDTO.getDishes().isEmpty()){
            marketingMapper.insertActivityDish(activityRule.getId(), marketingActivityDTO.getDishes());//重新插入活动关联的菜品
        }
    }

    @Override
    public PageResult<MarketingActSimpleVO> selectActivityList(Integer page, Integer pageSize, String name, Boolean vip, Integer type) {
        Integer total = marketingMapper.countActivityList(name, vip, type);
        Integer skip = (page - 1) * pageSize;
        List<MarketingActSimpleVO> marketingCSimpleVOS = marketingMapper.selectActivityList(skip, pageSize, name, vip, type);
        return new PageResult<>(total, marketingCSimpleVOS);
    }

    @Override
    public MarketingActivityVO selectActivityByAId(Integer id) {
        MarketingActivityVO marketingActivityVO = marketingMapper.selectFullActivityByAId(id);//获取活动信息
        //处理数据
        switch (marketingActivityVO.getActivityType()){
            case 0:
                marketingActivityVO.setDiscount(null);
                marketingActivityVO.setStartTime(null);
                marketingActivityVO.setEndTime(null);
                break;
            case 1:
                marketingActivityVO.setGiveNum(null);
                marketingActivityVO.setStartTime(null);
                marketingActivityVO.setEndTime(null);
                break;
            case 2:
                marketingActivityVO.setBuyNum(null);
                marketingActivityVO.setGiveNum(null);
                break;
        }
        marketingActivityVO.setDepts(marketingMapper.selectActivityDeptsByAId(id));//获取活动关联的部门
        marketingActivityVO.setCombos(marketingMapper.selectActivityCombosByAId(id));//获取活动关联的套餐
        marketingActivityVO.setDishes(marketingMapper.selectActivityDishesByAId(id));//获取活动关联的菜品
        return marketingActivityVO;
    }

    @Override
    public Integer updateCouponInvByCId(Integer couponId, Integer num) {
        return marketingMapper.updateCouponInvByCId(couponId,num);
    }

    @Override
    public void batchAddCouponInv(List<OrderCouponDTO> couponNumList) {
        // 按优惠券ID分组求和，避免重复更新同一条记录
        Map<Integer, Integer> couponSumMap = couponNumList.stream()
                .collect(Collectors.groupingBy(
                        OrderCouponDTO::getCouponId,
                        Collectors.summingInt(OrderCouponDTO::getNum)
                ));
        couponNumList = couponSumMap.entrySet().stream()
                .map(entry -> new OrderCouponDTO(entry.getKey(), entry.getValue()))
                .toList();
        marketingMapper.batchAddCouponInv(couponNumList);
    }

    @Override
    public void insertCouponToClient(Integer couponId, Integer loginUserId, Integer num) {
        List<Integer> Num = new ArrayList<>();
        for (int i = 0; i < num; i++) {Num.add(i);}
        marketingMapper.insertCouponToClient(couponId,loginUserId,Num);
    }

    @Override
    public void batchInsertCouponToClient(List<OrderCouRollbackDTO> clientCouponList) {
        List<ClientCoupon> clientCoupons = new ArrayList<>();
        for (OrderCouRollbackDTO item : clientCouponList) {
            for (int i = 0; i < item.getNum(); i++) {
                ClientCoupon clientCoupon = new ClientCoupon();
                clientCoupon.setCouponId(item.getCouponId());
                clientCoupon.setClientId(item.getClientId());
                clientCoupons.add(clientCoupon);
            }
        }
        marketingMapper.batchInsertCouponToClient(clientCoupons);
    }

    @Override
    public void updateCouponUseNum(List<MarketingCUseNum> couponUseNumList) {
        marketingMapper.updateCouponUseNum(couponUseNumList);
    }
}
