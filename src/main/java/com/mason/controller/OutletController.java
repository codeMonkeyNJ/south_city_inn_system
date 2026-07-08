package com.mason.controller;

import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.dto.OutletGoodsDTO;
import com.mason.domain.dto.OutletStateDTO;
import com.mason.domain.vo.OutletGoodsVO;
import com.mason.service.OutletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys/outlet")
public class OutletController {
    @Autowired
    private OutletService outletService;
    /**
     * 根据门店id获取门店商品列表
     * 权限码: outlet-goods-select
     * 数据范围:
     * 1:允许获取用户所属门店的商品列表
     * 0:允许获取所有门店的商品列表
     */
    @GetMapping("/goods")
    @AuthCode("outlet-goods-select")
    public Result selectOutletGoodsList(HttpServletRequest request,
                                        Integer id,
                                        Integer page,
                                        Integer pageSize,
                                        String name,
                                        String className,
                                        Integer type) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        PageResult<OutletGoodsVO> pageResult = outletService.selectOutletGoodsList(loginUserId,dataCoverage,id,page,pageSize,name,className,type);
        return Result.success(pageResult);
    }
    /**
     * 修改门店的商品状态
     * 权限码: outlet-goods-update
     * 数据范围:
     * 1:允许修改用户所属门店的商品状态
     * 0:允许修改所有门店的商品状态
     */
    @PutMapping("/goods")
    @AuthCode("outlet-goods-update")
    public Result updateOutletGoodsState(HttpServletRequest request,@RequestBody OutletGoodsDTO outletGoodsDTO) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        outletService.updateOutletGoodsState(loginUserId,dataCoverage,outletGoodsDTO);
        return Result.success();
    }

    /**
     * 修改门店营业状态
     * 权限码: outlet-state-update
     * 数据范围:
     * 1:允许修改用户所属门店的营业状态
     * 0:允许修改所有门店的营业状态
     */
    @PutMapping("/state")
    @AuthCode("outlet-state-update")
    public Result updateOutletState(HttpServletRequest request, @RequestBody OutletStateDTO outletStateDTO) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        outletService.updateOutletState(loginUserId,dataCoverage,outletStateDTO);
        return Result.success();
    }
}
