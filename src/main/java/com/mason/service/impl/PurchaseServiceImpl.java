package com.mason.service.impl;

import com.mason.domain.PageResult;
import com.mason.domain.dto.*;
import com.mason.domain.po.Purchase;
import com.mason.domain.po.PurchaseApply;
import com.mason.domain.po.PurchaseDetail;
import com.mason.domain.po.Supplier;
import com.mason.domain.vo.*;
import com.mason.exception.AuthorityException;
import com.mason.exception.BusinessException;
import com.mason.mapper.PurchaseMapper;
import com.mason.service.*;
import com.mason.utils.UniqueNo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private PurchaseMapper purchaseMapper;
    @Autowired
    private UniqueNo uniqueNo;
    @Autowired
    private DeptService deptService;
    @Autowired
    private UserService userService;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryExService inventoryExService;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    @Transactional
    public void insertPurchaseApply(Integer loginUserId, Integer dataCoverage, PurchaseApplyDTO purchaseApplyDTO) {
        switch (dataCoverage){
            case 2:
                if (!deptService.getDeptIdsByUserId(loginUserId).contains(purchaseApplyDTO.getDeptId())){throw new AuthorityException("权限不足");}
                break;
            case 1:
                List<Integer> deptIds = deptService.getAllDeptIdsByUserId(loginUserId);
                if (!deptIds.contains(purchaseApplyDTO.getDeptId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        PurchaseApply purchaseApply = new PurchaseApply();
        BeanUtils.copyProperties(purchaseApplyDTO,purchaseApply);//拷贝属性
        purchaseApply.setNo(uniqueNo.getUniqueNo("PA"));//生成采购申请单号
        purchaseApply.setApplicantId(loginUserId);//设置申请人id
        purchaseMapper.insertPurchaseApply(purchaseApply);//插入采购申请
        if (purchaseApplyDTO.getDetail() == null || purchaseApplyDTO.getDetail().isEmpty()){
            throw new BusinessException("采购申请单的采购明细不能为空");
        }
        List<PurchaseDetail> details = purchaseApplyDTO.getDetail().stream().map(detail -> {
            PurchaseDetail purchaseDetail = new PurchaseDetail();
            BeanUtils.copyProperties(detail, purchaseDetail);
            purchaseDetail.setSourceType(1);
            purchaseDetail.setSourceId(purchaseApply.getId());
            return purchaseDetail;
        }).toList();
        List<String> fields = Arrays.asList("source_type", "source_id", "material_id", "plan_num");
        purchaseMapper.insertPurchaseDetail(fields,details);//插入采购申请单的采购明细
        rabbitTemplate.convertAndSend("webSocket_notify_exchange","webSocket_notify_key",new WebSocketNotifyDTO(purchaseApply.getId(),WebSocketNotifyDTO.PURCHASE_APPLY));//

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurchaseApply(Integer loginUserId, Integer dataCoverage, PurchaseApplyDTO purchaseApplyDTO) {
        //权限校验
        switch (dataCoverage){
            case 2:
                Integer applicantId = purchaseMapper.selectApplicantIdByPAId(purchaseApplyDTO.getId());//查询采购申请单的申请人id
                if (!Objects.equals(applicantId, loginUserId)){throw new AuthorityException("权限不足");}
                if (!inventoryService.selectStoreIdsByUserId(loginUserId).contains(purchaseApplyDTO.getStoreId())){throw new AuthorityException("权限不足");}
                break;
            case 1:
                List<Integer> validDeptIds = deptService.getAllDeptIdsByUserId(loginUserId);
                Integer applicantDeptId = purchaseMapper.selectADIdByPAId(purchaseApplyDTO.getId());//查询采购申请单的申请部门id
                if (!validDeptIds.contains(applicantDeptId)){throw new AuthorityException("权限不足");}
                if (!inventoryService.selectAllStoreIdsByUserId(loginUserId).contains(purchaseApplyDTO.getStoreId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        PurchaseApply purchaseApply = purchaseMapper.selectPAByPAId(purchaseApplyDTO.getId());//查询采购申请单
        if (purchaseApply==null){throw new BusinessException("采购申请单不存在");}
        if (purchaseApply.getState()!=0){throw new BusinessException("采购申请单已被处理,无法修改");}
        if (purchaseApplyDTO.getState()!=null){
            //状态为驳回时需要填写驳回原因
            if (purchaseApplyDTO.getState()==2 && purchaseApplyDTO.getCause()==null){
                throw new BusinessException("请填写驳回原因");
            }
            //状态不是驳回时，驳回原因置空
            if (purchaseApplyDTO.getState()!=2){
                purchaseApplyDTO.setCause(null);
            }
        }else{
            purchaseApplyDTO.setCause(null);
        }
        BeanUtils.copyProperties(purchaseApplyDTO,purchaseApply);
        Integer updateCount = purchaseMapper.updatePurchaseApply(purchaseApply);//修改采购申请单
        if (updateCount==0){throw new BusinessException("采购申请单不存在");}
        if (purchaseApplyDTO.getDetail() == null || purchaseApplyDTO.getDetail().isEmpty()){
            throw new BusinessException("采购申请单的采购明细不能为空");
        }
        //删除采购申请单对应的采购明细
        purchaseMapper.deletePDByPAId(purchaseApplyDTO.getId());//删除采购申请单的采购明细
        //收集新采购申请的采购明细
        List<PurchaseDetail> details = purchaseApplyDTO.getDetail().stream().map(detail -> {
            PurchaseDetail purchaseDetail = new PurchaseDetail();
            BeanUtils.copyProperties(detail, purchaseDetail);
            purchaseDetail.setSourceType(1);
            purchaseDetail.setSourceId(purchaseApply.getId());
            return purchaseDetail;
        }).toList();
        List<String> fields = Arrays.asList("source_type", "source_id", "material_id", "plan_num");
        purchaseMapper.insertPurchaseDetail(fields,details);//插入新采购申请单的采购明细

    }

    @Override
    public void updatePAStateToReject(Integer loginUserId, Integer dataCoverage, PurchaseApplyRejectDTO purchaseApplyRejectDTO) {
        //权限校验
        switch (dataCoverage){
            case 1:
                List<Integer> validDeptIds = deptService.getAllDeptIdsByUserId(loginUserId);
                Integer applicantDeptId = purchaseMapper.selectADIdByPAId(purchaseApplyRejectDTO.getId());//查询采购申请单的申请部门id
                if (!validDeptIds.contains(applicantDeptId)){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        PurchaseApply purchaseApply = purchaseMapper.selectPAByPAId(purchaseApplyRejectDTO.getId());//查询采购申请单
        if (purchaseApply==null){throw new BusinessException("采购申请单不存在");}
        if (purchaseApply.getState()==1){throw new BusinessException("采购申请单已被受理,无法驳回");}
        //驳回时需要填写驳回原因
        if (purchaseApplyRejectDTO.getCause()==null){
            throw new BusinessException("请填写驳回原因");
        }
        purchaseApply.setState(2);
        BeanUtils.copyProperties(purchaseApplyRejectDTO,purchaseApply);
        purchaseMapper.updatePurchaseApply(purchaseApply);//修改采购申请单
    }

    @Override
    public PageResult<PurchaseApplySimpleVO> queryPurchaseApply(Integer loginUserId,
                                                                Integer dataCoverage,
                                                                Integer page,
                                                                Integer pageSize,
                                                                String no,
                                                                String dept,
                                                                String applicant,
                                                                String state,
                                                                LocalDate stateTime,
                                                                LocalDate endTime) {
        Integer total;
        List<PurchaseApplySimpleVO> items;
        List<Integer> validDeptIds = null;
        Integer skip = (page - 1) * pageSize;
        switch (dataCoverage){
            case 2:
                applicant = userService.selectNameByUserId(loginUserId);//强制修改查询条件用户名称
                break;
            case 1:
                validDeptIds = deptService.getAllDeptIdsByUserId(loginUserId);//获取当前用户合法的部门id列表
                break;
            case 0:
                break;
        }
        total = purchaseMapper.countPAList(no,dept,applicant,state,stateTime,endTime,validDeptIds);//查询采购申请数量
        if (total==0){return new PageResult<>(0,null);}
        items = purchaseMapper.selectPAList(skip,pageSize,no,dept,applicant,state,stateTime,endTime,validDeptIds);//查询采购申请列表
        return new PageResult<>(total,items);
    }

    @Override
    public PurchaseApplyVO selectPurchaseApplyById(Integer loginUserId, Integer dataCoverage, Integer id) {
        PurchaseApplyVO purchaseApplyVO = purchaseMapper.selectFullPAByPAId(id);//查询采购申请单基础信息
        if (purchaseApplyVO==null){throw new BusinessException("采购申请单不存在");}
        List<Integer> validDeptIds;
        switch (dataCoverage){
            case 2:
                if (!Objects.equals(purchaseApplyVO.getApplicantId(), loginUserId)){throw new AuthorityException("权限不足");}
                purchaseApplyVO.setDetail(materialService.selectMaterialByPAId(id));
                break;
            case 1:
                validDeptIds = deptService.getAllDeptIdsByUserId(loginUserId);//获取当前用户合法的部门id列表
                if(!validDeptIds.contains(purchaseApplyVO.getDeptId())){throw new AuthorityException("权限不足");}
                purchaseApplyVO.setDetail(materialService.selectMaterialByPAId(id));
                break;
            case 0:
                purchaseApplyVO.setDetail(materialService.selectMaterialByPAId(id));
                break;
        }
        return purchaseApplyVO;
    }

    @Override
    public PurchaseApply selectBasePurchaseApplyById(Integer orderId) {
        return purchaseMapper.selectPAByPAId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDTO,supplier);
        try {
            purchaseMapper.insertSupplier(supplier);//插入供应商
        } catch (DuplicateKeyException e) {
            throw new BusinessException("供应商已存在");
        }
        if (supplierDTO.getMaterials()==null || supplierDTO.getMaterials().isEmpty()){return;}
        purchaseMapper.insertSupplierMaterial(supplier.getId(),supplierDTO.getMaterials());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierDTO,supplier);
        Integer updateCount = purchaseMapper.updateSupplier(supplier);//修改供应商
        if (updateCount==0){throw new BusinessException("供应商不存在");}
        purchaseMapper.deleteSMBySupplierId(supplierDTO.getId());//删除原有的供应商的物料关系
        purchaseMapper.insertSupplierMaterial(supplier.getId(),supplierDTO.getMaterials());//重新插入供应商的物料关系
    }

    @Override
    public PageResult<PurchaseSupplierSimpleVO> selectSupplierList(Integer page, Integer pageSize, String name) {
        Integer total = purchaseMapper.countSupplier(name);//查询供应商数量
        if (total==0){return new PageResult<>(0,null);}
        Integer skip = (page - 1) * pageSize;
        List<PurchaseSupplierSimpleVO> items = purchaseMapper.selectSupplierList(skip,pageSize,name);
        return new PageResult<>(total,items);
    }

    @Override
    public PurchaseSupplierVO selectSupplierById(Integer id) {
        PurchaseSupplierVO purchaseSupplierVO = purchaseMapper.selectSupplierById(id);
        if (purchaseSupplierVO==null){throw new BusinessException("供应商不存在");}
        List<PurchaseSupplierMaterialVO> materials = materialService.selectMaterialBySupplierId(id);
        purchaseSupplierVO.setMaterials( materials);
        return purchaseSupplierVO;
    }

    @Override
    public PageResult<PurchaseSupplierItemVO> selectSupplierByMaterialId(Integer id) {
        List<PurchaseSupplierItemVO> items = purchaseMapper.selectSupplierByMaterialId(id);
        return new PageResult<>(items.size(),items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSupplier(List<Integer> ids) {
        //删除供应商与物料的关系
        purchaseMapper.deleteSupplierMaterial(ids);
        //删除供应商
        purchaseMapper.deleteSupplier(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertPurchaseOrder(Integer loginUserId, Integer dataCoverage, PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseApply purchaseApply = purchaseMapper.selectPAByPAId(purchaseOrderDTO.getApplyId());//查询采购申请单
        switch (dataCoverage){
            case 2:
                if(purchaseOrderDTO.getApplyId() != null){
                    if(!deptService.getDeptIdsByUserId(loginUserId).contains(purchaseApply.getDeptId())){throw new AuthorityException("权限不足");}//修改后的部门id不在当前用户权限内
                    if(!inventoryService.selectStoreIdsByUserId(loginUserId).contains(purchaseApply.getStoreId())){throw new AuthorityException("权限不足");}//修改后的仓库id不在当前用户权限内
                }else {
                    if(!deptService.getDeptIdsByUserId(loginUserId).contains(purchaseOrderDTO.getDeptId())){throw new AuthorityException("权限不足");}//修改后的部门id不在当前用户权限内
                    if(!inventoryService.selectStoreIdsByUserId(loginUserId).contains(purchaseOrderDTO.getStoreId())){throw new AuthorityException("权限不足");}//修改后的仓库id不在当前用户权限内
                }
                break;
            case 1:
                if(purchaseOrderDTO.getApplyId() != null){
                    if(!deptService.getAllDeptIdsByUserId(loginUserId).contains(purchaseApply.getDeptId())){throw new AuthorityException("权限不足");}//修改后的部门id不在当前用户权限内
                    if(!inventoryService.selectAllStoreIdsByUserId(loginUserId).contains(purchaseApply.getStoreId())){throw new AuthorityException("权限不足");}//修改后的仓库id不在当前用户权限内
                }else {
                    if(!deptService.getAllDeptIdsByUserId(loginUserId).contains(purchaseOrderDTO.getDeptId())){throw new AuthorityException("权限不足");}//修改后的部门id不在当前用户权限内
                    if(!inventoryService.selectAllStoreIdsByUserId(loginUserId).contains(purchaseOrderDTO.getStoreId())){throw new AuthorityException("权限不足");}//修改后的仓库id不在当前用户权限内
                }
                break;
            case 0:
                break;
        }
        String no = uniqueNo.getUniqueNo("PO");//生成采购单号
        if (purchaseOrderDTO.getApplyId() != null){//当前采购订单有关联的采购申请单
            if (!purchaseApply.getState().equals(0)){throw new BusinessException("该申请已被受理");}
            purchaseApply.setState(1);//修改关联的采购申请单状态为已受理
            purchaseMapper.updatePurchaseApply(purchaseApply);//修改采购申请单状态为已受理
            //插入采购单的基本信息
            Purchase purchase = new Purchase();
            BeanUtils.copyProperties(purchaseOrderDTO,purchase);
            purchase.setNo(no);
            purchase.setBuyerId(loginUserId);
            purchaseMapper.insertPurchaseOrder(purchase);//插入采购单
            //修改关联的采购申请单采购明细
            List<PurchaseDetail> details = purchaseOrderDTO.getDetail().stream().map(detail -> {
                PurchaseDetail purchaseDetail = new PurchaseDetail();
                purchaseDetail.setMaterialId(detail.getMaterialId());
                purchaseDetail.setSupplierId(detail.getSupplierId());
                purchaseDetail.setRealNum(detail.getRealNum());
                purchaseDetail.setMoney(detail.getMoney());
                purchaseDetail.setRemark(detail.getRemark());
                return purchaseDetail;
            }).toList();
            List<String> fields = Arrays.asList("supplier_id","real_num","money","remark");//需要修改的字段
            purchaseMapper.updatePurchaseDetail(1,purchaseOrderDTO.getApplyId(),fields,details);
        }else{//当前采购订单无关联的采购申请单
            //插入采购单的基本信息
            Purchase purchase = new Purchase();
            BeanUtils.copyProperties(purchaseOrderDTO,purchase);
            purchase.setNo(no);
            purchase.setBuyerId(loginUserId);
            purchaseMapper.insertPurchaseOrder(purchase);
            //插入采购单的采购明细
            if (purchaseOrderDTO.getDetail()==null || purchaseOrderDTO.getDetail().isEmpty()){return;}
            List<PurchaseDetail> details = purchaseOrderDTO.getDetail().stream().map(detail -> {
                PurchaseDetail purchaseDetail = new PurchaseDetail();
                BeanUtils.copyProperties(detail,purchaseDetail);
                purchaseDetail.setSourceType(0);
                purchaseDetail.setSourceId(purchase.getId());
                purchaseDetail.setRealNum(detail.getRealNum()==null?0:detail.getRealNum());
                purchaseDetail.setMoney(detail.getMoney()==null?0f:detail.getMoney());
                return purchaseDetail;
            }).toList();
            List<String> fields = Arrays.asList("source_type","source_id","material_id","supplier_id","plan_num","real_num","money","remark");//需要添加的字段
            purchaseMapper.insertPurchaseDetail(fields,details);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurchaseOrder(Integer loginUserId, Integer dataCoverage, PurchaseOrderDTO purchaseOrderDTO) {
        Purchase purchase = purchaseMapper.selectPOByPOId(purchaseOrderDTO.getId());
        if (purchase ==  null){throw new BusinessException("采购订单不存在");}
        PurchaseApply purchaseApply = new PurchaseApply();
        if (purchaseOrderDTO.getApplyId()!= null){
            purchaseApply = purchaseMapper.selectPAByPAId(purchaseOrderDTO.getApplyId());//查询采购申请单
            if (purchaseApply == null){throw new BusinessException("采购申请单不存在");}
        }
        if (purchase.getApplyId() != null && !Objects.equals(purchaseOrderDTO.getApplyId(), purchase.getApplyId())){throw new BusinessException("不允许修改采购申请");}
        if (!purchase.getState().equals(0)){throw new BusinessException("无法修改待入库或已入库的采购订单");}
        switch (dataCoverage){
            case 2:
                //采购订单的采购员必须是当前用户
                if (!loginUserId.equals(purchase.getBuyerId())){throw new AuthorityException("权限不足");}
                if(purchaseOrderDTO.getApplyId() != null){
                    if(!deptService.getDeptIdsByUserId(loginUserId).contains(purchaseApply.getDeptId())){throw new AuthorityException("权限不足");}//修改后的部门id不在当前用户权限内
                    if(!inventoryService.selectStoreIdsByUserId(loginUserId).contains(purchaseApply.getStoreId())){throw new AuthorityException("权限不足");}//修改后的仓库id不在当前用户权限内
                }else {
                    if(!deptService.getDeptIdsByUserId(loginUserId).contains(purchaseOrderDTO.getDeptId())){throw new AuthorityException("权限不足");}//修改后的部门id不在当前用户权限内
                    if(!inventoryService.selectStoreIdsByUserId(loginUserId).contains(purchaseOrderDTO.getStoreId())){throw new AuthorityException("权限不足");}//修改后的仓库id不在当前用户权限内
                }
                break;
            case 1:
                //采购订单的所属部门必须是当前用户所在部门或其子部门
                if (!deptService.getAllDeptIdsByUserId(loginUserId).contains(purchase.getDeptId())){throw new AuthorityException("权限不足");}
                if(purchaseOrderDTO.getApplyId() != null){
                    if(!deptService.getAllDeptIdsByUserId(loginUserId).contains(purchaseApply.getDeptId())){throw new AuthorityException("权限不足");}//修改后的部门id不在当前用户权限内
                    if(!inventoryService.selectAllStoreIdsByUserId(loginUserId).contains(purchaseApply.getStoreId())){throw new AuthorityException("权限不足");}//修改后的仓库id不在当前用户权限内
                }else {
                    if(!deptService.getAllDeptIdsByUserId(loginUserId).contains(purchaseOrderDTO.getDeptId())){throw new AuthorityException("权限不足");}//修改后的部门id不在当前用户权限内
                    if(!inventoryService.selectAllStoreIdsByUserId(loginUserId).contains(purchaseOrderDTO.getStoreId())){throw new AuthorityException("权限不足");}//修改后的仓库id不在当前用户权限内
                }
                break;
            case 0:
                break;
        }
        if (purchaseOrderDTO.getApplyId() != null){//当前采购订单有关联的采购申请单
            if (purchase.getApplyId() == null){//原采购订单无关联的采购申请单
                if (!purchaseApply.getState().equals(0)){throw new BusinessException("该申请已被受理");}
                purchaseApply.setState(1);//修改关联的采购申请单状态为已受理
                purchaseMapper.updatePurchaseApply(purchaseApply);//修改采购申请单状态为已受理
            }
            //修改采购单的基本信息
            BeanUtils.copyProperties(purchaseOrderDTO,purchase);
            purchase.setBuyerId(loginUserId);
            purchaseMapper.updatePurchaseOrder(purchase);//修改采购单
            //修改关联的采购申请单采购明细
            List<PurchaseDetail> details = purchaseOrderDTO.getDetail().stream().map(detail -> {
                PurchaseDetail purchaseDetail = new PurchaseDetail();
                purchaseDetail.setMaterialId(detail.getMaterialId());
                purchaseDetail.setSupplierId(detail.getSupplierId());
                purchaseDetail.setRealNum(detail.getRealNum());
                purchaseDetail.setMoney(detail.getMoney());
                purchaseDetail.setRemark(detail.getRemark());
                return purchaseDetail;
            }).toList();
            List<String> fields = Arrays.asList("supplier_id","real_num","money","remark");//需要修改的字段
            purchaseMapper.updatePurchaseDetail(1,purchaseOrderDTO.getApplyId(),fields,details);
        }else{//当前采购订单无关联的采购申请单
            //修改采购单的基本信息
            BeanUtils.copyProperties(purchaseOrderDTO,purchase);
            purchase.setBuyerId(loginUserId);
            Integer orderId = purchaseMapper.insertPurchaseOrder(purchase);
            //删除原有的采购明细
            purchaseMapper.deletePurchaseDetail(0,purchaseOrderDTO.getId());
            //插入采购单的采购明细
            if (purchaseOrderDTO.getDetail()==null || purchaseOrderDTO.getDetail().isEmpty()){return;}
            List<PurchaseDetail> details = purchaseOrderDTO.getDetail().stream().map(detail -> {
                PurchaseDetail purchaseDetail = new PurchaseDetail();
                BeanUtils.copyProperties(detail,purchaseDetail);
                purchaseDetail.setSourceType(0);
                purchaseDetail.setSourceId(orderId);
                purchaseDetail.setRealNum(detail.getRealNum()==null?0:detail.getRealNum());
                purchaseDetail.setMoney(detail.getMoney()==null?0f:detail.getMoney());
                return purchaseDetail;
            }).toList();
            List<String> fields = Arrays.asList("source_type","source_id","material_id","supplier_id","plan_num","real_num","money","remark");//需要添加的字段
            purchaseMapper.insertPurchaseDetail(fields,details);
        }
    }

    @Override
    public PageResult<PurchaseOrderSimpleVO> selectPOList(Integer loginUserId,
                                                          Integer dataCoverage,
                                                          Integer page,
                                                          Integer pageSize,
                                                          String dept,
                                                          String buyer,
                                                          String stocker,
                                                          String store,
                                                          String no,
                                                          Integer state) {
        List<Integer> validDeptIds = null;
        switch (dataCoverage){
            case 2:
                validDeptIds = deptService.getDeptIdsByUserId(loginUserId);//当前用户所属部门id列表
                break;
            case 1:
                validDeptIds = deptService.getAllDeptIdsByUserId(loginUserId);//当前用户所属部门及子部门id列表
                break;
            case 0:
                break;
        }
        Integer total = purchaseMapper.CountPOList(validDeptIds,dept,buyer,stocker,store,no,state);
        Integer skip = (page-1)*pageSize;
        List<PurchaseOrderSimpleVO> items = purchaseMapper.selectFullPOList(validDeptIds,dept,skip,pageSize,buyer,stocker,store,no,state);
        return new PageResult<>(total,items);
    }

    @Override
    public PurchaseOrderVO selectPOByPOId(Integer loginUserId, Integer dataCoverage, Integer id) {
        PurchaseOrderVO purchaseOrderVO = purchaseMapper.selectFullPOByPOId(id);//获取采购单基本信息
        if (purchaseOrderVO == null){throw new BusinessException("采购单不存在");}
        switch (dataCoverage){
            case 2:
                if (!Objects.equals(purchaseOrderVO.getBuyerId(), loginUserId) && !Objects.equals(purchaseOrderVO.getStockerId(), loginUserId)){
                    throw new BusinessException("权限不足");
                }
                break;
            case 1:
                if (!deptService.getAllDeptIdsByUserId(loginUserId).contains(purchaseOrderVO.getDeptId())){
                    throw new BusinessException("权限不足");
                }
            case 0:
                break;
        }
        //获取采购单的采购明细
        List<PurchaseFullDetailVO> detail;
        if (purchaseOrderVO.getApplyId()!=null){//当前采购单有关联的采购申请单
            detail = purchaseMapper.selectFullPDByPOOrPAId(1,purchaseOrderVO.getApplyId());//获取采购申请单的采购明细
        }else {//当前采购单无关联的采购申请单
            detail = purchaseMapper.selectFullPDByPOOrPAId(0,id);//获取采购单的采购明细
        }
        purchaseOrderVO.setDetail(detail);
        return purchaseOrderVO;
    }

    @Override
    public void updatePOStateToStandby(Integer loginUserId, Integer dataCoverage, Integer orderId) {
        Purchase purchase = purchaseMapper.selectPOByPOId(orderId);
        switch (dataCoverage){
            case 2:
                if (!Objects.equals(purchase.getBuyerId(), loginUserId)){throw new BusinessException("权限不足");}
                break;
            case 1:
                if (!deptService.getAllDeptIdsByUserId(loginUserId).contains(purchase.getDeptId())){throw new BusinessException("权限不足");}
            case 0:
                break;
        }
        if (purchase.getStoreId() == null){throw new BusinessException("未填写入库仓库");}
        List<PurchaseDetail> details;
        if (purchase.getApplyId()!= null){
            details = purchaseMapper.selectPDByPOOrPAId(1, purchase.getApplyId());
        }else{
            details = purchaseMapper.selectPDByPOOrPAId(0, orderId);
        }
        details.forEach(item->{
            if (item.getSupplierId() == null || item.getRealNum() == 0){
                throw new BusinessException("采购明细未填写完整");
            }
        });
        purchaseMapper.updatePOStateToStandby(orderId);//修改采购单状态为待入库
    }

    @Override
    @Transactional
    public void updatePOStateToFinish(Integer loginUserId, Integer dataCoverage, Integer orderId) {
        Purchase purchase = purchaseMapper.selectPOByPOId(orderId);
        switch (dataCoverage){
            case 2:
                if (!deptService.getDeptIdsByUserId(loginUserId).contains(purchase.getDeptId())){throw new BusinessException("权限不足");}
                break;
            case 1:
                if (!deptService.getAllDeptIdsByUserId(loginUserId).contains(purchase.getDeptId())){throw new BusinessException("权限不足");}
            case 0:
                break;
        }
        if (purchase.getState() != 1){throw new BusinessException("当前采购订单不支持入库");}
        //获取采购明细
        List<PurchaseDetail> details = purchase.getApplyId() == null?purchaseMapper.selectPDByPOOrPAId(0, orderId):purchaseMapper.selectPDByPOOrPAId(1, purchase.getApplyId());
        //修改库存
        details.forEach(item-> inventoryExService.updateRepertory(0,orderId,purchase.getStoreId(),item.getMaterialId(),0,item.getRealNum()));
        purchaseMapper.updatePOStateToFinish(orderId,loginUserId);//修改采购单状态为入库完成
    }
}
