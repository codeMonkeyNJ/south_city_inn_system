package com.mason.mapper;

import com.mason.domain.po.Purchase;
import com.mason.domain.po.PurchaseApply;
import com.mason.domain.po.PurchaseDetail;
import com.mason.domain.po.Supplier;
import com.mason.domain.vo.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PurchaseMapper {
    /**
     * 添加采购申请
     */
    void insertPurchaseApply(PurchaseApply purchaseApply);

    /**
     * 添加采购明细
     */
    void insertPurchaseDetail(List<String> fields,List<PurchaseDetail> details);

    /**
     * 修改采购申请
     */
    Integer updatePurchaseApply(PurchaseApply purchaseApply);

    /**
     * 根据采购单id查询申请人id
     */
    @Select("select applicant_id from purchase_apply where id=#{id}")
    Integer selectApplicantIdByPAId(Integer id);

    /**
     * 根据采购申请id查询申请部门id
     */
    @Select("select dept_id from purchase_apply where id= #{id}")
    Integer selectADIdByPAId(Integer id);

    /**
     * 根据采购申请单id删除采购明细
     */
    @Delete("delete from purchase_detail where source_id=#{id} and source_type=1")
    void deletePDByPAId(Integer id);

    /**
     * 查询采购申请列表数量
     */
    Integer countPAList(String no, String dept, String applicant, String state, LocalDate stateTime, LocalDate endTime, List<Integer> validDeptIds);

    /**
     * 查询采购申请列表
     */
    List<PurchaseApplySimpleVO> selectPAList(Integer skip, Integer pageSize, String no, String dept, String applicant, String state, LocalDate stateTime, LocalDate endTime, List<Integer> validDeptIds);

    /**
     * 根据采购申请单id查询采购申请单（联表查询完整的采购申请单基础信息）
     */
    @Select("""
        select pa.no,pa.dept_id,d.name as dept,pa.store_id,s.name as store,applicant_id,ui.name as applicant,pa.state,p.id as purchaseId,pa.remark,cause,pa.create_time as applyTime
        from purchase_apply pa
        left join purchase p on p.apply_id = pa.id
        join store s on pa.store_id = s.id
        join dept d on pa.dept_id = d.id
        join user_info ui on pa.applicant_id = ui.user_id
        where pa.id = #{id}
    """)
    PurchaseApplyVO selectFullPAByPAId(Integer id);

    /**
     * 根据采购申请单id查询采购申请单（仅查询包含采购申请单表中的信息，没有联表查询）
     */
    @Select("select * from purchase_apply where id = #{id}")
    PurchaseApply selectPAByPAId(Integer id);
    /**
     * 添加供应商
     */
    void insertSupplier(Supplier supplier);

    /**
     * 添加供应商物料关系
     */
    void insertSupplierMaterial(Integer supplierId, List<Integer> materials);

    /**
     * 修改供应商
     */
    Integer updateSupplier(Supplier supplier);

    /**
     * 删除供应商物料关系
     */
    @Delete("delete from supplier_material where supplier_id = #{supplierId}")
    void deleteSMBySupplierId(Integer supplierId);

    /**
     * 统计供应商数量
     */
    Integer countSupplier(String name);

    /**
     * 查询供应商列表
     */
    List<PurchaseSupplierSimpleVO> selectSupplierList(Integer skip, Integer pageSize, String name);

    /**
     * 根据供应商id查询供应商
     */
    @Select("select name,address,phone,link_name,account_name,bank,account from supplier where id = #{id}")
    PurchaseSupplierVO selectSupplierById(Integer id);

    /**
     * 删除供应商物料关系
     */
    void deleteSupplierMaterial(List<Integer> ids);

    /**
     * 删除供应商
     */
    void deleteSupplier(List<Integer> ids);

    /**
     * 添加采购订单
     */
    Integer insertPurchaseOrder(Purchase  purchase);

    /**
     * 修改采购订单
     * @param sourceType 采购明细的来源类型（0采购订单/1采购申请）
     * @param sourceId 采购明细的来源id（采购订单id/采购申请id）
     * @param fields 修改的字段
     * @param details 修改的采购明细
     */
    void updatePurchaseDetail(Integer sourceType,Integer sourceId,List<String> fields ,List<PurchaseDetail> details);

    /**
     * 根据采购订单id查询采购订单
     * @param id 采购订单id
     */
    @Select("select * from purchase where id = #{id}")
    Purchase selectPOByPOId(Integer id);

    /**
     * 修改采购订单
     */
    Integer updatePurchaseOrder(Purchase purchase);

    /**
     * 根据采购申请id查询采购申请单状态
     */
    @Select("select state from purchase_apply where id = #{applyId}")
    Integer selectPAStateByPAId(Integer applyId);

    /**
     * 删除采购明细
     * @param sourceType 采购明细的来源类型（0采购订单/1采购申请）
     * @param orderId 采购明细的来源id（采购订单id/采购申请id）
     */
    @Delete("delete from purchase_detail where source_type = #{sourceType} and source_id = #{orderId}")
    void deletePurchaseDetail(Integer sourceType, Integer orderId);

    /**
     * 统计采购订单数量
     */
    Integer CountPOList(List<Integer> validDeptIds, String dept, String buyer, String stocker, String store, String no, Integer state);

    /**
     * 查询采购订单列表
     */
    List<PurchaseOrderSimpleVO> selectFullPOList(List<Integer> validDeptIds, String dept, Integer skip, Integer pageSize, String buyer, String stocker, String store, String no, Integer state);

    /**
     * 根据采购订单id查询采购订单（仅查询包含采购订单表中的详细信息，前端展示使用）
     */
    @Select("""
        select p.no,p.apply_id,pa.no as applyNo,p.dept_id as deptId,d.name as dept,p.buyer_id,ui1.name as buyer,p.stocker_id, ui2.name as stocker,p.store_id,s.name as store,p.state,p.money,p.remark
        from purchase p
        left join purchase_apply pa on p.apply_id = pa.id
        left join dept d on p.dept_id = d.id
        left join user_info ui1 on p.buyer_id = ui1.user_id
        left join user_info ui2 on p.stocker_id = ui2.user_id
        left join store s on p.store_id = s.id
        where p.id = #{id};
    """)
    PurchaseOrderVO selectFullPOByPOId(Integer id);

    /**
     * 根据采购订单或采购申请查询采购物料列表
     * @param sourceType 采购明细的来源类型（0采购订单/1采购申请）
     * @param sourceId 采购明细的来源id（采购订单id/采购申请id）
     */
    @Select("""
        select m.id,m.name,s.id as supplierId,s.name as supplier,pd.plan_num,pd.real_num,m.pack as unit,pd.money,pd.remark
        from purchase_detail pd
        join material m on pd.material_id = m.id
        join supplier s on pd.supplier_id = s.id
        where pd.source_type = #{sourceType} and pd.source_id = #{sourceId};
    """)
    List<PurchaseFullDetailVO> selectFullPDByPOOrPAId(Integer sourceType, Integer sourceId);

    /**
     * 根据采购订单或采购申请查询采购明细列表(只包含数据库字段)
     * @param sourceType 采购明细的来源类型（0采购订单/1采购申请）
     * @param sourceId 采购明细的来源id（采购订单id/采购申请id）
     */
    @Select("select id, source_type, source_id, material_id, supplier_id, plan_num, real_num, money, remark, update_time, create_time from purchase_detail where source_type = #{sourceType} and source_id = #{sourceId};")
    List<PurchaseDetail> selectPDByPOOrPAId(Integer sourceType, Integer sourceId);

    /**
     * 采购订单状态修改为待入库
     */
    @Update("update purchase set state = 1 where id = #{orderId}")
    void updatePOStateToStandby(Integer orderId);

    /**
     * 采购订单状态修改为完成
     */
    @Update("update purchase set state = 2,stocker_id = #{loginUserId} where id = #{orderId}")
    void updatePOStateToFinish(Integer orderId, Integer loginUserId);

    /**
     * 根据物料id查询供应商列表
     * @param id 物料id
     */
    @Select("""
        select s.id,s.name from supplier_material sm
        join supplier s on s.id = sm.supplier_id
        where sm.material_id = #{id};
    """)
    List<PurchaseSupplierItemVO> selectSupplierByMaterialId(Integer id);
}
