package com.mason.service;

import com.mason.domain.PageResult;
import com.mason.domain.dto.PurchaseApplyDTO;
import com.mason.domain.dto.PurchaseApplyRejectDTO;
import com.mason.domain.dto.PurchaseOrderDTO;
import com.mason.domain.dto.SupplierDTO;
import com.mason.domain.po.PurchaseApply;
import com.mason.domain.vo.*;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseService {

    /**
     * 添加采购申请
     */
    void insertPurchaseApply(Integer loginUserId, Integer dataCoverage, PurchaseApplyDTO purchaseApplyDTO);

    /**
     * 修改采购申请
     */
    void updatePurchaseApply(Integer loginUserId, Integer dataCoverage, PurchaseApplyDTO purchaseApplyDTO);

    /**
     * 修改采购申请状态为已拒绝
     */
    void updatePAStateToReject(Integer loginUserId, Integer dataCoverage, PurchaseApplyRejectDTO purchaseApplyRejectDTO);

    /**
     * 查询采购申请列表
     */
    PageResult<PurchaseApplySimpleVO> queryPurchaseApply(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, String no, String dept, String applicant, String state, LocalDate stateTime, LocalDate endTime);

    /**
     * 获取采购申请详情
     */
    PurchaseApplyVO selectPurchaseApplyById(Integer loginUserId, Integer dataCoverage, Integer id);

    /**
     * 根据id获取采购申请（基本信息）
     */
    PurchaseApply selectBasePurchaseApplyById(Integer orderId);

    /**
     * 添加供应商
     */
    void insertSupplier(SupplierDTO supplierDTO);

    /**
     * 修改供应商
     */
    void updateSupplier(SupplierDTO supplierDTO);

    /**
     * 获取供应商列表
     */
    PageResult<PurchaseSupplierSimpleVO> selectSupplierList(Integer page, Integer pageSize, String name);

    /**
     * 获取供应商详情
     */
    PurchaseSupplierVO selectSupplierById(Integer id);

    /**
     * 根据物料id查询供应商列表
     * @param id 物料id
     */
    PageResult<PurchaseSupplierItemVO> selectSupplierByMaterialId(Integer id);

    /**
     * 删除供应商
     */
    void deleteSupplier(List<Integer> ids);

    /**
     * 添加采购订单
     */
    void insertPurchaseOrder(Integer loginUserId, Integer dataCoverage, PurchaseOrderDTO purchaseOrderDTO);

    /**
     * 修改采购订单
     */
    void updatePurchaseOrder(Integer loginUserId, Integer dataCoverage, PurchaseOrderDTO purchaseOrderDTO);

    /**
     * 获取采购订单列表
     */
    PageResult<PurchaseOrderSimpleVO> selectPOList(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, String dept, String buyer, String stocker, String store, String no, Integer state);

    /**
     * 获取采购订单详情
     */
    PurchaseOrderVO selectPOByPOId(Integer loginUserId, Integer dataCoverage, Integer orderId);

    /**
     * 修改采购订单状态为待入库
     */
    void updatePOStateToStandby(Integer loginUserId, Integer dataCoverage, Integer id);

    /**
     * 修改采购订单状态为已入库
     */
    void updatePOStateToFinish(Integer loginUserId, Integer dataCoverage, Integer id);


}
