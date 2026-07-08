package com.mason.controller;

import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.dto.MarketingActivityDTO;
import com.mason.domain.dto.MarketingCouponDTO;
import com.mason.domain.vo.MarketingActSimpleVO;
import com.mason.domain.vo.MarketingActivityVO;
import com.mason.domain.vo.MarketingCSimpleVO;
import com.mason.domain.vo.MarketingCouponVO;
import com.mason.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/marketing")
public class MarketingController {
    @Autowired
    private MarketingService marketingService;
    /**
     * 添加优惠券
     * 权限码: marketing-coupon-insert
     */
    @PostMapping("/coupon")
    @AuthCode("marketing-coupon-insert")
    public Result insertCoupon(@RequestBody MarketingCouponDTO marketingCouponDTO) {
        marketingService.insertCoupon(marketingCouponDTO);
        return Result.success();
    }

    /**
     * 修改优惠券
     * 权限码: marketing-coupon-update
     */
    @PutMapping("/coupon")
    @AuthCode("marketing-coupon-update")
    public Result updateCoupon(@RequestBody MarketingCouponDTO marketingCouponDTO) {
        marketingService.updateCoupon(marketingCouponDTO);
        return Result.success();
    }

    /**
     * 修改优惠券状态
     * 权限码: marketing-coupon-state-update
     */
    @PutMapping("/coupon/state")
    @AuthCode("marketing-coupon-state-update")
    public Result updateCouponState(Integer id, Boolean state) {
        marketingService.updateCouponStateByCId(id, state);
        return Result.success();
    }

    /**
     * 获取优惠券列表
     * 权限码: marketing-coupon-select
     */
    @GetMapping("/coupon")
    @AuthCode("marketing-coupon-select")
    public Result selectCoupon(Integer page, Integer pageSize, String name,Integer type,Boolean state) {
        PageResult<MarketingCSimpleVO> pageResult = marketingService.selectCouponList(page, pageSize, name, type, state);
        return Result.success(pageResult);
    }
    /**
     * 根据优惠券id获取优惠券详情
     * 权限码: marketing-coupon-select-id
     */
    @GetMapping("/coupon/{id}")
    @AuthCode("marketing-coupon-select-id")
    public Result selectCouponById(@PathVariable Integer id) {
        MarketingCouponVO marketingCouponVO = marketingService.selectCouponByCId(id);
        return Result.success(marketingCouponVO);
    }
    /**
     * 批量删除优惠券
     * 权限码: marketing-coupon-delete
     */
    @DeleteMapping("/coupon")
    @AuthCode("marketing-coupon-delete")
    public Result deleteCoupon(@RequestParam List<Integer> ids) {
        marketingService.batchDeleteCoupon(ids);
        return Result.success();
    }

    /**
     * 添加活动
     * 权限码: marketing-activity-insert
     */
    @PostMapping("/activity")
    @AuthCode("marketing-activity-insert")
    public Result insertActivity(@RequestBody MarketingActivityDTO marketingActivityDTO) {
        marketingService.insertActivity(marketingActivityDTO);
        return Result.success();
    }
    /**
     * 修改活动
     * 权限码: marketing-activity-update
     */
    @PutMapping("/activity")
    @AuthCode("marketing-activity-update")
    public Result updateActivity(@RequestBody MarketingActivityDTO marketingActivityDTO) {
        marketingService.updateActivity(marketingActivityDTO);
        return Result.success();
    }
    /**
     * 获取活动列表
     * 权限码: marketing-activity-select
     */
    @GetMapping("/activity")
    @AuthCode("marketing-activity-select")
    public Result selectActivity(Integer page, Integer pageSize, String name,Boolean vip,Integer type) {
        PageResult<MarketingActSimpleVO> pageResult = marketingService.selectActivityList(page, pageSize, name, vip, type);
        return Result.success(pageResult);
    }

    /**
     * 根据活动id获取活动详情
     * 权限码: marketing-activity-select-id
     */
    @GetMapping("/activity/{id}")
    @AuthCode("marketing-activity-select-id")
    public Result selectActivityByAId(@PathVariable Integer id) {
        MarketingActivityVO marketingActivityVO = marketingService.selectActivityByAId(id);
        return Result.success(marketingActivityVO);
    }
}
