package com.mason.controller;

import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.dto.PurchaseApplyDTO;
import com.mason.domain.dto.PurchaseApplyRejectDTO;
import com.mason.domain.dto.PurchaseOrderDTO;
import com.mason.domain.dto.SupplierDTO;
import com.mason.domain.vo.*;
import com.mason.service.PurchaseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sys/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 获取采购申请列表
     * 权限码：purchase-apply-select
     * 数据范围：
     * 2：用户本人创建的采购申请
     * 1：用户所在部门及子部门的采购申请
     * 0：所有采购申请
     */
    @GetMapping("/apply")
    @AuthCode("purchase-apply-select")
    public Result selectPurchaseApply(HttpServletRequest request,
                                      Integer page,
                                      Integer pageSize,
                                      String no,
                                      String dept,
                                      String applicant,
                                      String state,
                                      LocalDate stateTime,
                                      LocalDate endTime) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        PageResult<PurchaseApplySimpleVO> pageResult = purchaseService.queryPurchaseApply(loginUserId, dataCoverage, page, pageSize, no, dept, applicant, state, stateTime, endTime);
        return Result.success(pageResult);
    }
    /**
     * 根据采购申请id获取采购申请详情
     * 权限码：purchase-apply-select-id
     * 数据范围：
     * 2：用户本人创建的采购申请
     * 1：用户所在部门及子部门的采购申请
     * 0：所有采购申请
     */
    @GetMapping("/apply/{id}")
    @AuthCode("purchase-apply-select-id")
    public Result selectPurchaseApplyById(HttpServletRequest request, @PathVariable Integer id) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        PurchaseApplyVO purchaseApplyVO = purchaseService.selectPurchaseApplyById(loginUserId, dataCoverage, id);
        return Result.success(purchaseApplyVO);
    }

    /**
     * 添加采购申请
     * 权限码：purchase-apply-insert
     * 数据范围：
     * 2：用户所在部门的采购申请
     * 1：用户所在部门及子部门的采购申请
     * 0：所有采购申请
     */
    @PostMapping("/apply")
    @AuthCode("purchase-apply-insert")
    public Result insertPurchaseApply(HttpServletRequest request, @RequestBody PurchaseApplyDTO purchaseApplyDTO) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        purchaseService.insertPurchaseApply(loginUserId, dataCoverage, purchaseApplyDTO);
        return Result.success();
    }

    /**
     * 修改采购申请
     * 权限码：purchase-apply-update
     * 数据范围：
     * 2：用户自己创建的采购申请
     * 1：用户所在部门及子部门的采购申请
     * 0：所有采购申请
     */
    @PutMapping("/apply")
    @AuthCode("purchase-apply-update")
    public Result updatePurchaseApply(HttpServletRequest request, @RequestBody PurchaseApplyDTO purchaseApplyDTO) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        purchaseService.updatePurchaseApply(loginUserId, dataCoverage, purchaseApplyDTO);
        return Result.success();
    }

    /**
     * 驳回采购申请
     * 权限码：purchase-order-update
     * 数据范围：
     * 1：允许驳回用户所属部门及子部门的采购申请
     * 0：允许驳回所有采购申请
     */
    @PutMapping("/apply/reject")
    @AuthCode("purchase-order-update")
    public Result updatePAStateToReject(HttpServletRequest request, @RequestBody PurchaseApplyRejectDTO purchaseApplyRejectDTO) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        purchaseService.updatePAStateToReject(loginUserId, dataCoverage, purchaseApplyRejectDTO);
        return Result.success();
    }

    /**
     * 添加供应商
     */
    @PostMapping("/supplier")
    @AuthCode("purchase-supplier-insert")
    public Result insertSupplier(@RequestBody SupplierDTO supplierDTO) {
        purchaseService.insertSupplier(supplierDTO);
        return Result.success();
    }
    /**
     * 修改供应商
     */
    @PutMapping("/supplier")
    @AuthCode("purchase-supplier-update")
    public Result updateSupplier(@RequestBody SupplierDTO supplierDTO) {
        purchaseService.updateSupplier(supplierDTO);
        return Result.success();
    }
    /**
     * 获取供应商列表
     */
    @GetMapping("/supplier")
    @AuthCode("purchase-supplier-select")
    public Result selectSupplierList(Integer page, Integer pageSize, String name){
        PageResult<PurchaseSupplierSimpleVO> pageResult =  purchaseService.selectSupplierList(page, pageSize, name);
        return Result.success(pageResult);
    }
    /**
     * 根据供应商id查询供应商详情
     */
    @GetMapping("/supplier/{id}")
    @AuthCode("purchase-supplier-select-id")
    public Result selectSupplierById(@PathVariable Integer id){
        PurchaseSupplierVO purchaseSupplierVO = purchaseService.selectSupplierById(id);
        return Result.success(purchaseSupplierVO);
    }

    /**
     * 根据物料id查询提供该物料的供应商列表
     */
    @GetMapping("/supplier/material/{id}")
    @AuthCode("purchase-supplier-select-material-id")
    public Result selectSupplierByMaterialId(@PathVariable Integer id){
        PageResult<PurchaseSupplierItemVO> pageResult = purchaseService.selectSupplierByMaterialId(id);
        return Result.success(pageResult);
    }

    /**
     * 删除供应商
     */
    @DeleteMapping("/supplier")
    @AuthCode("purchase-supplier-delete")
    public Result deleteSupplier(@RequestParam List<Integer> ids){
        purchaseService.deleteSupplier(ids);
        return Result.success();
    }

    /**
     * 添加采购订单
     * 权限码：purchase-order-insert
     * 数据范围：
     * 2：允许添加用户所在部门的采购订单
     * 1：允许添加用户所在部门及子部门的采购订单
     * 0：允许添加所有采购订单
     */
    @PostMapping
    @AuthCode("purchase-order-insert")
    public Result insertPurchaseOrder(HttpServletRequest request, @RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        purchaseService.insertPurchaseOrder(loginUserId, dataCoverage, purchaseOrderDTO);
        return Result.success();
    }

    /**
     * 修改采购订单
     * 权限码：purchase-order-update
     * 数据范围：
     * 2：允许修改用户本人创建的采购订单,且仓库id只能修改为用户所在部门的仓库
     * 1：允许修改用户所属部门及子部门的采购订单,且仓库id只能修改为用户所在部门及其子部门的仓库
     * 0：允许修改所有采购订单
     * 说明：该接口只允许修改状态为采购中的采购订单
     */
    @PutMapping
    @AuthCode("purchase-order-update")
    public Result updatePurchaseOrder(HttpServletRequest request, @RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        purchaseService.updatePurchaseOrder(loginUserId, dataCoverage, purchaseOrderDTO);
        return Result.success();
    }

    /**
     * 修改采购订单状态为待入库
     * 权限码：purchase-order-standby-update
     * 数据范围：
     * 2：允许修改用户本人创建的采购订单状态
     * 1：允许修改用户所在部门及子部门的采购订单状态
     * 0：允许修改所有采购订单状态
     * 说明：该接口只允许将采购订单状态修改为待入库
     */
    @PutMapping("/standby/{id}")
    @AuthCode("purchase-order-standby-update")
    public Result updatePOStateToStandby(HttpServletRequest request, @PathVariable Integer id) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        purchaseService.updatePOStateToStandby(loginUserId,dataCoverage,id);
        return Result.success();
    }
    /**
     * 修改采购订单状态为已入库
     * 权限码：purchase-order-finish-select
     * 数据范围：
     * 2：允许修改用户所在部门的采购订单
     * 1：允许修改用户所在部门及子部门的采购订单
     * 0：允许修改所有采购订单
     * 说明：该接口只允许修改状态为待入库的采购订单
     */
    @PutMapping("/finish/{id}")
    @AuthCode("purchase-order-finish-update")
    public Result updatePOStateToFinish(HttpServletRequest request, @PathVariable Integer id) {
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        purchaseService.updatePOStateToFinish(loginUserId,dataCoverage,id);
        return Result.success();
    }

    /**
     * 获取采购订单列表
     * 权限码：purchase-order-select
     * 数据范围：
     * 2：允许查看用户所在部门的采购订单
     * 1：允许查看用户所在部门及子部门的采购订单
     * 0：允许查看所有采购订单
     */
    @GetMapping
    @AuthCode("purchase-order-select")
    public Result selectPurchaseOrderList(HttpServletRequest request,
                                          Integer page,
                                          Integer pageSize,
                                          String dept,
                                          String buyer,
                                          String stocker,
                                          String store,
                                          String no,
                                          Integer state){
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        PageResult<PurchaseOrderSimpleVO> pageResult = purchaseService.selectPOList(loginUserId, dataCoverage, page, pageSize, dept, buyer, stocker, store, no, state);
        return Result.success(pageResult);
    }
    /**
     * 根据采购订单id查询采购订单详情
     * 权限码：purchase-order-select-id
     * 数据范围：
     * 2：允许查看用户本人负责的采购订单
     * 1：允许查看用户所在部门及子部门的采购订单
     * 0：允许查看所有采购订单
     */
    @GetMapping("/{id}")
    @AuthCode("purchase-order-select-id")
    public Result selectPOByPOId(HttpServletRequest request, @PathVariable Integer id){
        Integer loginUserId = (Integer)request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer)request.getAttribute("dataCoverage");
        PurchaseOrderVO purchaseOrderVO = purchaseService.selectPOByPOId(loginUserId, dataCoverage, id);
        return Result.success(purchaseOrderVO);
    }
}
