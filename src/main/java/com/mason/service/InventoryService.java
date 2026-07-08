package com.mason.service;

import com.mason.domain.PageResult;
import com.mason.domain.SlidePageResult;
import com.mason.domain.dto.*;
import com.mason.domain.vo.*;

import java.util.Date;
import java.util.List;

public interface InventoryService {
    /**
     * 添加仓库
     */
    void insertStore(Integer loginUserId, Integer dataCoverage, InventoryStoreDTO inventoryStoreDTO);

    /**
     * 修改仓库
     */
    void updateStore(Integer loginUserId, Integer dataCoverage, InventoryStoreDTO inventoryStoreDTO);

    /**
     * 查询仓库列表
     */
    PageResult<InventoryStoreSimpleVO> selectStoreList(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, String name);

    /**
     * 根据仓库id获取其库存列表
     */
    PageResult<InventoryMaterialVO> selectMaterialListByStoreId(Integer loginUserId, Integer dataCoverage, Integer id, Integer page, Integer pageSize, String name, String className);

    /**
     * 根据库存id查询物料出入库记录
     */
    SlidePageResult<InventoryMaterialLogVO> selectStoreLogByRepertoryId(Integer loginUserId, Integer dataCoverage, Integer id, Integer pageSize, Integer lastId, Integer operation, Date startDate, Date endDate);

    /**
     * 获取用户所属部门的仓库ids
     */
    List<Integer> selectStoreIdsByUserId(Integer userId);

    /**
     * 获取用户所属部门及其子部门的仓库ids
     */
    List<Integer> selectAllStoreIdsByUserId(Integer userId);

    /**
     * 根据部门id获取其仓库id列表
     */
    List<Integer> selectStoreIdsByDeptId(Integer deptId);

    /**
     * 获取用户所属部门的仓库列表
     */
    PageResult<InventoryStoreSimplerVO> selectStoreByUserId(Integer userId);

    /**
     * 添加要货单
     */
    void insertEnquiry(Integer loginUserId,Integer dataCoverage, InventoryEnquiryDTO inventoryEnquiryDTO);

    /**
     * 支付要货单
     */
    void payEnquiry(Integer loginUserId, InventoryEnquiryPayDTO inventoryEnquiryPayDTO);

    /**
     * 取消要货单
     */
    void cancelEnquiry(Integer loginUserId, InventorEnquiryUpdateDTO inventorEnquiryUpdateDTO);

    /**
     * 申请退款
     */
    void applyRefundEnquiry(Integer loginUserId, InventorEnquiryUpdateDTO inventorEnquiryUpdateDTO);

    /**
     * 处理退款
     */
    void handleRefundEnquiry(Integer loginUserId, InventorEnquiryUpdateDTO inventorEnquiryUpdateDTO);

    /**
     * 获取要货单列表
     */
    PageResult<InvEnqSimVO> selectEnquiryList(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, Integer no, Integer state, String dept, Integer minMoney, Integer maxMoney);

    /**
     * 获取要货单详情
     */
    InvEnqVO selectEnquiryByEId(Integer loginUserId, Integer dataCoverage, Integer id);

    /**
     * 添加出库单
     **/
    void insertOutbound(Integer loginUserId, Integer dataCoverage, InvOutDTO invOutDTO);

    /**
     * 修改出库单
     **/
    void updateOutbound(Integer loginUserId, Integer dataCoverage, InvOutDTO invOutDTO);

    /**
     * 修改出库单为待接收
     */
    void updateOutboundToDelivery(Integer loginUserId, InvOutStateDTO invOutStateDTO);

    /**
     * 获取出库单列表
     */
    PageResult<InvOutSimVO> selectOutboundList(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, String no, String stocker, String store, String dept, Integer state);

    /**
     * 根据出库单id获取出库单详情
     */
    InvOutVO selectOutboundByOId(Integer loginUserId, Integer dataCoverage, Integer id);

    /**
     * 修改出库单为已完成
     */
    void updateOutboundToFinish(Integer loginUserId, InvOutStateDTO invOutStateDTO);

    /**
     * 添加库存修正单
     */
    void insertStoreAmend(Integer loginUserId, Integer dataCoverage, InvAmendDTO invAmendDTO);

    /**
     * 修改库存修正单状态
     */
    void updateStoreAmendState(Integer loginUserId, Integer dataCoverage, InvAmendDTO invAmendDTO);

    /**
     * 获取库存修正单列表
     */
    PageResult<InvAmendSimpleVO> selectStoreAmendList(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, Integer state, String dept);

    /**
     * 根据库存修正单id获取库存修正单详情
     */
    InvAmendVO selectStoreAmendById(Integer loginUserId, Integer dataCoverage, Integer id);

}
