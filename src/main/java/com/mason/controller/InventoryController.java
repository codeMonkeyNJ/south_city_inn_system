package com.mason.controller;

import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.SlidePageResult;
import com.mason.domain.dto.*;
import com.mason.domain.vo.*;
import com.mason.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/sys/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;
    /**
     * 添加仓库
     * 权限码: inventory-store-insert
     * 权限范围：
     * 2：允许添加用户所属部门的仓库
     * 1：允许添加用户所属部门及其子部门仓库
     * 0：允许添加所有仓库
     */
    @PostMapping("/store")
    @AuthCode("inventory-store-insert")
    public Result insertStore(HttpServletRequest request, @RequestBody InventoryStoreDTO inventoryStoreDTO) {
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        inventoryService.insertStore(loginUserId,dataCoverage,inventoryStoreDTO);
        return Result.success();
    }
    /**
     * 修改仓库
     * 权限码：inventory-store-update
     * 数据范围：
     * 2：允许修改用户所属部门的仓库
     * 1：允许修改用户所属部门及其子部门仓库
     * 0：允许修改所有仓库
     */
    @PutMapping("/store")
    @AuthCode("inventory-store-update")
    public Result updateStore(HttpServletRequest request, @RequestBody InventoryStoreDTO inventoryStoreDTO) {
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        inventoryService.updateStore(loginUserId,dataCoverage,inventoryStoreDTO);
        return Result.success();
    }

    /**
     * 获取仓库列表
     * 权限码：inventory-store-list
     * 数据范围：
     * 2：允许获取用户所属部门的仓库列表
     * 1：允许获取用户所属部门及其子部门仓库列表
     * 0：允许获取所有仓库列表
     */
    @GetMapping("/store")
    @AuthCode("inventory-store-select")
    public Result selectStoreList(HttpServletRequest request,Integer page, Integer pageSize, String name) {
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        PageResult<InventoryStoreSimpleVO> pageResult = inventoryService.selectStoreList(loginUserId,dataCoverage,page,pageSize,name);
        return Result.success(pageResult);
    }

    /**
     * 获取登录用户所属部门的仓库列表
     */
    @GetMapping("/store/user")
    public Result selectStoreByUserId(HttpServletRequest request) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        PageResult<InventoryStoreSimplerVO> pageResult = inventoryService.selectStoreByUserId(loginUserId);
        return Result.success(pageResult);
    }

    /**
     * 根据仓库id获取其库存列表
     * 权限码：inventory-repertory-select-store-id
     * 数据范围：
     * 2：允许获取用户所属部门的仓库库存
     * 1：允许获取用户所属部门及其子部门仓库库存
     * 0：允许获取所有仓库库存
     */
    @GetMapping("/store/{id}")
    @AuthCode("inventory-repertory-select-store-id")
    public Result selectStoreById(HttpServletRequest request,
                                  @PathVariable Integer id,
                                  Integer page,
                                  Integer pageSize,
                                  String name,
                                  String className) {
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        PageResult<InventoryMaterialVO> pageResult = inventoryService.selectMaterialListByStoreId(loginUserId,dataCoverage,id,page,pageSize,name,className);
        return Result.success(pageResult);
    }

    /**
     * 根据库存id查询物料出入库记录
     * 权限码：inventory-log-select-repertory-id
     * 数据范围：
     * 2：允许获取用户所属部门的仓库的物料出入库记录
     * 1：允许获取用户所属部门及其子部门仓库的物料出入库记录
     * 0：允许获取所有仓库的物料出入库记录
     */
    @GetMapping("/store/log/repertory/{id}")
    @AuthCode("inventory-log-select-repertory-id")
    public Result selectStoreLogByInventoryId(HttpServletRequest request,
                                             @PathVariable Integer id,
                                             Integer pageSize,
                                             Integer lastId,
                                             Integer operation,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                  Date startDate,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                  Date endDate){
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        SlidePageResult<InventoryMaterialLogVO> pageResult = inventoryService.selectStoreLogByRepertoryId(loginUserId,dataCoverage,id,pageSize,lastId,operation,startDate,endDate);
        return Result.success(pageResult);
    }
    /**
     * 添加要货单
     *  权限码：inventory-enquiry-insert
     *  数据范围：
     *  2：允许添加用户所属部门的要货单
     *  1：允许添加用户所属部门及其子部门要货单
     *  0：允许添加所有要货单
     */
    @PostMapping("/enquiry")
    @AuthCode("inventory-enquiry-insert")
    public Result insertEnquiry(HttpServletRequest request, @RequestBody InventoryEnquiryDTO inventoryEnquiryDTO) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        inventoryService.insertEnquiry(loginUserId,dataCoverage,inventoryEnquiryDTO);
        return Result.success();
    }

    /**
     * 支付要货单
     */
    @PutMapping("/enquiry/pay")
    public Result payEnquiry(HttpServletRequest request, @RequestBody InventoryEnquiryPayDTO inventoryEnquiryPayDTO) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        inventoryService.payEnquiry(loginUserId,inventoryEnquiryPayDTO);
        return Result.success();
    }

    /**
     * 取消要货单
     * 权限码：inventory-enquiry-cancel-update
     */
    @PutMapping("/enquiry/cancel")
    @AuthCode("inventory-enquiry-cancel-update")
    public Result cancelEnquiry(HttpServletRequest request, @RequestBody InventorEnquiryUpdateDTO inventorEnquiryUpdateDTO ) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        inventoryService.cancelEnquiry(loginUserId,inventorEnquiryUpdateDTO);
        return Result.success();
    }
    /**
     * 申请退款要货单
     * 权限码：inventory-enquiry-refund-apply-update
     */
    @PutMapping("/enquiry/refund/apply")
    @AuthCode("inventory-enquiry-refund-apply-update")
    public Result applyRefundEnquiry(HttpServletRequest request, @RequestBody InventorEnquiryUpdateDTO inventorEnquiryUpdateDTO) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        inventoryService.applyRefundEnquiry(loginUserId,inventorEnquiryUpdateDTO);
        return Result.success();
    }
    /**
     * 处理要货单退款
     * 权限码：inventory-enquiry-refund-handle-update
     * 权限范围：
     * 要货单未关联出库单时允许要货单所属部门的父部门内的人员处理退款
     * 要货单关联出库单后只允许创建出库单的人员处理退款
     */
    @PutMapping("/enquiry/refund/handle")
    @AuthCode("inventory-enquiry-refund-handle-update")
    public Result handleRefundEnquiry(HttpServletRequest request, @RequestBody InventorEnquiryUpdateDTO inventorEnquiryUpdateDTO) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        inventoryService.handleRefundEnquiry(loginUserId,inventorEnquiryUpdateDTO);
        return Result.success();
    }
    /**
     * 获取要货单列表
     * 权限码：inventory-enquiry-select
     * 数据范围：
     * 2：允许获取用户本人创建的要货单
     * 1：允许获取用户所属部门及其直属子部门的要货单
     * 0：允许获取所有要货单
     */
    @GetMapping("/enquiry")
    @AuthCode("inventory-enquiry-select")
    public Result selectEnquiryList(HttpServletRequest request,
                                    Integer page,
                                    Integer pageSize,
                                    Integer no,
                                    Integer state,
                                    String dept,
                                    Integer minMoney,
                                    Integer maxMoney){
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        PageResult<InvEnqSimVO> pageResult = inventoryService.selectEnquiryList(loginUserId,dataCoverage,page,pageSize,no,state,dept,minMoney,maxMoney);
        return Result.success(pageResult);
    }

    /**
     *根据id获取要货单
     * 权限码：inventory-enquiry-select-id
     * 数据范围：
     * 2：允许获取用户本人创建的要货单
     * 1：允许获取用户所属部门及其直属子部门的要货单
     * 0：允许获取所有要货单
     */
    @GetMapping("/enquiry/{id}")
    @AuthCode("inventory-enquiry-select-id")
    public Result selectEnquiryById(HttpServletRequest request,@PathVariable Integer id){
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        InvEnqVO invEnqVO = inventoryService.selectEnquiryByEId(loginUserId,dataCoverage,id);
        return Result.success(invEnqVO);
    }

    /**
     * 添加出库单
     * 权限码：inventory-outbound-insert
     * 数据范围：
     * 2：允许添加用户所属部门对应仓库的出库单
     * 1：允许添加用户所属部门及其子部门对应仓库的出库单(危险，容易出现责任问题)
     * 0：允许添加所有出库单(危险，容易出现责任问题)
     */
    @PostMapping("/outbound")
    @AuthCode("inventory-outbound-insert")
    public Result insertOutbound(HttpServletRequest request, @RequestBody InvOutDTO invOutDTO) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        inventoryService.insertOutbound(loginUserId,dataCoverage,invOutDTO);
        return Result.success();
    }

    /**
     * 修改出库单
     * 权限码：purchase-order-update
     * 数据范围：
     * 2：允许修改用户自己创建的并且状态为待出库的出库单
     * 1：允许修改用户所属部门及其子部门对应仓库的出库单(危险,容易出现责任问题)
     * 0：允许修改所有出库单(危险，容易出现责任问题)
     */
    @PutMapping("/outbound")
    @AuthCode("inventory-outbound-update")
    public Result updateOutbound(HttpServletRequest request, @RequestBody InvOutDTO invOutDTO) {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        inventoryService.updateOutbound(loginUserId,dataCoverage,invOutDTO);
        return Result.success();
    }
    /**
     * 获取出库单列表
     * 权限码：inventory-outbound-select
     * 数据范围：
     * 2：允许获取用户所属部门仓库的出库单
     * 1：允许获取用户所属部门及其子部门仓库的出库单
     * 0：允许获取所有出库单
     */
    @GetMapping("/outbound")
    @AuthCode("inventory-outbound-select")
    public Result selectOutboundList(HttpServletRequest request,
                                     Integer page,
                                     Integer pageSize,
                                     String no,
                                     String stocker,
                                     String store,
                                     String dept,
                                     Integer state){
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        PageResult<InvOutSimVO> pageResult = inventoryService.selectOutboundList(loginUserId,dataCoverage,page,pageSize,no,stocker,store,dept,state);
        return Result.success(pageResult);
    }
    /**
     * 根据id获取出库单
     * 权限码：inventory-outbound-select-id
     * 数据范围：
     * 2：允许获取用户所属部门仓库的出库单
     * 1：允许获取用户所属部门及其子部门仓库的出库单
     * 0：允许取所有出库单
     */
    @GetMapping("/outbound/{id}")
    @AuthCode("inventory-outbound-select-id")
    public Result selectOutboundById(HttpServletRequest request,@PathVariable Integer id){
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        InvOutVO invOutVO = inventoryService.selectOutboundByOId(loginUserId,dataCoverage,id);
        return Result.success(invOutVO);
    }
    /**
     * 修改出库单状态为待接收
     * 权限码：inventory-outbound-delivery-update
     * 只允许创建该出库单的仓库管理员修改
     */
    @PutMapping("/outbound/delivery")
    @AuthCode("inventory-outbound-delivery-update")
    public Result updateOutboundDelivery(HttpServletRequest request, @RequestBody InvOutStateDTO invOutStateDTO){
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        inventoryService.updateOutboundToDelivery(loginUserId,invOutStateDTO);
        return Result.success();
    }
    /**
     * 修改出库单状态为已完成
     * 权限码：inventory-outbound-finish-update
     * 只允许创建该出库单的仓库管理员修改
     */
    @PutMapping("/outbound/finish")
    @AuthCode("inventory-outbound-finish-update")
    public Result updateOutboundFinish(HttpServletRequest request, @RequestBody InvOutStateDTO invOutStateDTO){
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        inventoryService.updateOutboundToFinish(loginUserId,invOutStateDTO);
        return Result.success();
    }
    /**
     * 添加库存修正单
     * 权限码：inventory-amend-insert
     * 数据范围：
     * 1：允许添加用户所属部门的库存修正单
     * 0：允许添加所有库存修正单
     */
    @PostMapping("/store/amend")
    @AuthCode("inventory-amend-insert")
    public Result insertStoreAmend(HttpServletRequest request, @RequestBody InvAmendDTO invAmendDTO){
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        inventoryService.insertStoreAmend(loginUserId,dataCoverage,invAmendDTO);
        return Result.success();
    }

    /**
     * 修改库存修正单状态
     * 权限码：inventory-amend-state-update
     * 数据范围：
     * 2:允许修改用户所属部门的子部门仓库创建的库存修正单状态
     * 1：允许修改用户所属部门及其子部门仓库创建的库存修正单状态
     * 0：允许修改所有仓库的库存修正单
     */
    @PutMapping("/store/amend")
    @AuthCode("inventory-amend-state-updatet")
    public Result updateStoreAmendState(HttpServletRequest request, @RequestBody InvAmendDTO invAmendDTO){
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        inventoryService.updateStoreAmendState(loginUserId,dataCoverage,invAmendDTO);
        return Result.success();
    }
    /**
     * 获取库存修正单列表
     * 权限码：inventory-amend-select
     * 数据范围：
     * 2：允许获取用户所属部门仓库的库存修正单
     * 1：允许获取用户所属部门及其子部门仓库的库存修正单
     * 0：允许获取所有库存修正单
     */
    @GetMapping("/store/amend")
    @AuthCode("inventory-amend-select")
    public Result selectStoreAmendList(HttpServletRequest request,Integer page, Integer pageSize, Integer state, String dept){
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        PageResult<InvAmendSimpleVO> pageResult = inventoryService.selectStoreAmendList(loginUserId,dataCoverage,page,pageSize,state,dept);
        return Result.success(pageResult);
    }

    /**
     * 根据id获取库存修正单
     * 权限码：inventory-amend-select-id
     * 数据范围：
     * 2：允许获取用户所属部门仓库的库存修正单
     * 1：允许获取用户所属部门及其子部门仓库的库存修正单
     * 0：允许获取所有库存修正单
     */
    @GetMapping("/store/amend/{id}")
    @AuthCode("inventory-amend-select-id")
    public Result selectStoreAmendById(HttpServletRequest request,@PathVariable Integer id){
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        Integer dataCoverage = (Integer) request.getAttribute("dataCoverage");
        InvAmendVO invAmendVO = inventoryService.selectStoreAmendById(loginUserId,dataCoverage,id);
        return Result.success(invAmendVO);
    }
}
