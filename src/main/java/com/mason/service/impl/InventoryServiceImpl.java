package com.mason.service.impl;

import com.mason.domain.PageResult;
import com.mason.domain.SlidePageResult;
import com.mason.domain.dto.*;
import com.mason.domain.po.*;
import com.mason.domain.vo.*;
import com.mason.exception.AuthorityException;
import com.mason.exception.BusinessException;
import com.mason.mapper.InventoryMapper;
import com.mason.service.DeptService;
import com.mason.service.InventoryExService;
import com.mason.service.InventoryService;
import com.mason.service.MaterialService;
import com.mason.utils.UniqueNo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private DeptService deptService;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private UniqueNo uniqueNo;
    @Autowired
    private InventoryExService inventoryExService;

    @Override
    public void insertStore(Integer loginUserId, Integer dataCoverage, InventoryStoreDTO inventoryStoreDTO) {
        List<Integer> validDeptIds;//用户合法部门，用于验证权限
        switch (dataCoverage){
            case 2:
                validDeptIds = deptService.getDeptIdsByUserId(loginUserId);//获取用户所属部门id
                if (!validDeptIds.contains(inventoryStoreDTO.getDeptId())){throw new BusinessException("权限不足");}
                break;
            case 1:
                validDeptIds = deptService.getAllDeptIdsByUserId(loginUserId);//获取用户所属部门及子部门id
                if (!validDeptIds.contains(inventoryStoreDTO.getDeptId())){throw new BusinessException("权限不足");}
            case 0:
                break;
        }
        try{
            inventoryMapper.insertStore(inventoryStoreDTO);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("仓库已存在");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStore(Integer loginUserId, Integer dataCoverage, InventoryStoreDTO inventoryStoreDTO) {
        List<Integer> validDeptIds;//用户合法部门，用于验证权限
        Integer deptId;  //仓库所属部门id，用于验证权限
        switch (dataCoverage){
            case 2:

                deptId = inventoryMapper.getDeptIdByStoreId(inventoryStoreDTO.getId());//获取仓库所属部门
                validDeptIds = deptService.getDeptIdsByUserId(loginUserId);//获取用户所属部门id
                if (!validDeptIds.contains(deptId)){throw new BusinessException("权限不足");}
                break;
            case 1:
                deptId = inventoryMapper.getDeptIdByStoreId(inventoryStoreDTO.getId());//获取仓库所属部门
                validDeptIds = deptService.getAllDeptIdsByUserId(loginUserId);//获取用户所属部门及子部门id
                if (!validDeptIds.contains(deptId)){throw new BusinessException("权限不足");}
            case 0:
                break;
        }
        try{
            Integer updateCount = inventoryMapper.updateStore(inventoryStoreDTO);
            if (updateCount == 0){throw new BusinessException("仓库不存在");}
        }catch (DuplicateKeyException e){
            throw new BusinessException("仓库已存在");
        }
    }

    @Override
    public PageResult<InventoryStoreSimpleVO> selectStoreList(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, String name) {
        List<Integer> validDeptIds = null;//用户合法部门，用于验证权限
        switch (dataCoverage){
            case 2:
                validDeptIds = deptService.getDeptIdsByUserId(loginUserId);//获取用户所属部门id
                break;
            case 1:
                validDeptIds = deptService.getAllDeptIdsByUserId(loginUserId);//获取用户所属部门及子部门id
                break;
            case 0:
                break;
        }
        Integer total = inventoryMapper.countStore(name, validDeptIds);
        if (total == 0){return new PageResult<>(0, null);}
        Integer skip = (page - 1) * pageSize;
        List<InventoryStoreSimpleVO> items = inventoryMapper.selectStoreList(skip, pageSize, name, validDeptIds);
        return new PageResult<>(total, items);
    }

    @Override
    public PageResult<InventoryMaterialVO> selectMaterialListByStoreId(Integer loginUserId,
                                                                       Integer dataCoverage,
                                                                       Integer id,
                                                                       Integer page,
                                                                       Integer pageSize,
                                                                       String name,
                                                                       String className) {
        List<Integer> validDeptIds;
        Integer deptId;
        switch (dataCoverage){
            case 2:
                deptId = inventoryMapper.selectDeptIdByStoreId(id);//获取仓库所属部门
                validDeptIds = deptService.getDeptIdsByUserId(loginUserId);//获取用户所属部门id
                if (!validDeptIds.contains(deptId)){throw new BusinessException("权限不足");}
            case 1:
                deptId = inventoryMapper.selectDeptIdByStoreId(id);//获取仓库所属部门
                validDeptIds = deptService.getAllDeptIdsByUserId(loginUserId);//获取用户所属部门及子部门id
                if (!validDeptIds.contains(deptId)){throw new BusinessException("权限不足");}
            case 0:
                break;
        }
        Integer total = inventoryMapper.countSMByStoreId(id, name, className);
        if (total == 0){return new PageResult<>(0, null);}
        Integer skip = (page - 1) * pageSize;
        List<InventoryMaterialVO> items = inventoryMapper.selectSMByStoreId(id,skip, pageSize, name, className);//获取仓库库存列表
        return new PageResult<>(total, items);
    }

    @Override
    public SlidePageResult<InventoryMaterialLogVO> selectStoreLogByRepertoryId(Integer loginUserId,
                                                                               Integer dataCoverage,
                                                                               Integer id,
                                                                               Integer pageSize,
                                                                               Integer lastId,
                                                                               Integer operation,
                                                                               Date startDate,
                                                                               Date endDate) {
        Integer storeId;//该库存所属的仓库id（用于校验权限）
        List<Integer> validStoreIds;//用户合法仓库id列表（用于校验权限）
        switch (dataCoverage){
            case 2:
                storeId = inventoryMapper.selectStoreIdByRTId(id);//获取其所属仓库id
                validStoreIds = inventoryMapper.selectStoreIdsByUserId(loginUserId);//获取用户所属部门的仓库id
                if (!validStoreIds.contains(storeId)){throw new BusinessException("权限不足");}
                break;
            case 1:
                storeId = inventoryMapper.selectStoreIdByRTId(id);//获取其所属仓库id
                validStoreIds = inventoryMapper.selectAllStoreIdsByUserId(loginUserId);//获取用户所属部门及其子部门的仓库id
                if (!validStoreIds.contains(storeId)){throw new BusinessException("权限不足");}
            case 0:
                break;
        }
        Integer lastLogId = inventoryMapper.selectLastLogIdByRepertoryId(id, operation, startDate, endDate);//获取该库存最后日志id
        if (lastLogId == null){throw new BusinessException("没有该库存");}
        List<StoreMaterialLog> logs = inventoryMapper.selectLogByRepertoryId(id, pageSize, lastId, operation, startDate, endDate);//获取该库存日志列表
        //转换为前端所需数据
        List<InventoryMaterialLogVO> items = logs.stream().map(log -> {
            InventoryMaterialLogVO inventoryMaterialLogVO = new InventoryMaterialLogVO();
            BeanUtils.copyProperties(log, inventoryMaterialLogVO);
            inventoryMaterialLogVO.setOperationTime(log.getCreateTime());
            return inventoryMaterialLogVO;
        }).toList();
        Integer reqLastId = logs.get(logs.size() - 1).getId();//获取本次返回的最后一条日志id
        return new SlidePageResult<>(reqLastId, !reqLastId.equals(lastLogId), items);
    }

    @Override
    public List<Integer> selectStoreIdsByUserId(Integer userId) {
        return inventoryMapper.selectStoreIdsByUserId(userId);
    }

    @Override
    public List<Integer> selectAllStoreIdsByUserId(Integer userId) {
        return inventoryMapper.selectAllStoreIdsByUserId(userId);
    }

    @Override
    public List<Integer> selectStoreIdsByDeptId(Integer deptId) {
        return inventoryMapper.selectStoreIdsByDeptId(deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertEnquiry(Integer loginUserId,Integer dataCoverage, InventoryEnquiryDTO inventoryEnquiryDTO) {
        if(inventoryEnquiryDTO.getDeptId() == null || inventoryEnquiryDTO.getStoreId() == null){
            throw new BusinessException("部门id或仓库id不能为空");
        }
        switch (dataCoverage){
            case 2:
                if (!deptService.getDeptIdsByUserId(loginUserId).contains(inventoryEnquiryDTO.getDeptId())){throw new AuthorityException("权限不足");}
                if (!inventoryMapper.selectStoreIdsByUserId(loginUserId).contains(inventoryEnquiryDTO.getStoreId())){throw new AuthorityException("权限不足");}
                break;
            case 1:
                if (!deptService.getAllDeptIdsByUserId(loginUserId).contains(inventoryEnquiryDTO.getDeptId())){throw new AuthorityException("权限不足");}
                if (!inventoryMapper.selectAllStoreIdsByUserId(loginUserId).contains(inventoryEnquiryDTO.getStoreId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        List<Integer> validStoreIds = inventoryMapper.selectStoreIdsByDeptId(inventoryEnquiryDTO.getDeptId());//获取部门下的仓库列表，确保要货单的仓库id合法
        if (!validStoreIds.contains(inventoryEnquiryDTO.getStoreId())){throw new BusinessException("部门与仓库不匹配");}
        List<InventoryEnquiryDetailDTO> details = inventoryEnquiryDTO.getDetails();
        String no = uniqueNo.getUniqueNo("IE");//生成要货单号
        Enquiry enquiry = new Enquiry();
        BeanUtils.copyProperties(inventoryEnquiryDTO, enquiry);
        enquiry.setNo(no);
        enquiry.setApplicantId(loginUserId);
        List<EnquiryDetail> enquiryDetails = new ArrayList<>();//要货单明细
        if (deptService.selectDeptTypeById(inventoryEnquiryDTO.getDeptId()) == 1){//部门类型为门店
            enquiry.setState(0);
            List<Integer> materialIds = details.stream().map(InventoryEnquiryDetailDTO::getMaterialId).toList();//数据采购单明细中的物料id
            List<Material> materials = materialService.selectMListByMIds(List.of("id","price"),materialIds);//获取数据采购单明细中的id和售价
            //将materials转为Map集合，key为id，value为material
            Map<Integer, Material> materialMap = materials.stream().collect(Collectors.toMap(Material::getId, material -> material));
            BigDecimal amount = BigDecimal.ZERO;//采购单总价
            for (InventoryEnquiryDetailDTO detail : details) {
                BigDecimal rowMoney = BigDecimal.valueOf(materialMap.get(detail.getMaterialId()).getPrice() * detail.getNum());//单条明细的价格
                EnquiryDetail enquiryDetail = new EnquiryDetail();
                BeanUtils.copyProperties(detail,enquiryDetail);
                enquiryDetail.setMoney(rowMoney);
                enquiryDetails.add(enquiryDetail);
                amount = amount.add(rowMoney);
            }
            enquiry.setAmount(amount);
            inventoryMapper.insertEnquiry(enquiry);//插入要货单基本信息
            for (EnquiryDetail enquiryDetail : enquiryDetails) {
                enquiryDetail.setSourceType(1);
                enquiryDetail.setSourceId(enquiry.getId());
            }
        }else{//部门类型为部门
            enquiry.setState(1);
            inventoryMapper.insertEnquiry(enquiry);//插入要货单基本信息
            //构建要货单明细
            enquiryDetails = details.stream().map(detail -> {
                EnquiryDetail enquiryDetail = new EnquiryDetail();
                BeanUtils.copyProperties(detail, enquiryDetail);
                enquiryDetail.setMoney(BigDecimal.ZERO);
                enquiryDetail.setSourceType(1);
                enquiryDetail.setSourceId(enquiry.getId());
                return enquiryDetail;
            }).toList();
        }
        inventoryMapper.batchInsertED(List.of("source_type","source_id","material_id","num","money","remark"),enquiryDetails);//插入要货单明细
    }

    @Override
    public void payEnquiry(Integer loginUserId, InventoryEnquiryPayDTO inventoryEnquiryPayDTO) {
        Enquiry enquiry = inventoryMapper.selectEnquiryByEId(inventoryEnquiryPayDTO.getId());//查询要货单
        if (enquiry == null){throw new BusinessException("要货单不存在");}
        if (!Objects.equals(enquiry.getApplicantId(), loginUserId)){throw new AuthorityException("当前订单非本人订单");}
        if (!Objects.equals(enquiry.getState(), 0)){throw new BusinessException("当前要货单无法支付");}
        //TODO实现支付功能
        enquiry.setState(1);//TODO测试使用,后续应该在支付的回调接口中修改订单状态
        inventoryMapper.updateEnquiry(enquiry);//更新要货单状态
    }

    @Override
    public void cancelEnquiry(Integer loginUserId, InventorEnquiryUpdateDTO inventorEnquiryUpdateDTO) {
        Enquiry enquiry = inventoryMapper.selectEnquiryByEId(inventorEnquiryUpdateDTO.getId());//查询要货单
        if (enquiry == null){throw new BusinessException("要货单不存在");}
        if (!Objects.equals(enquiry.getApplicantId(), loginUserId)){throw new AuthorityException("权限不足");}
        if (!Objects.equals(enquiry.getState(), 0)){throw new BusinessException("已支付的要货单无法取消");}
        if (inventorEnquiryUpdateDTO.getReqCause() == null){throw new BusinessException("请填写取消原因");}
        enquiry.setState(7);
        enquiry.setReqCause(inventorEnquiryUpdateDTO.getReqCause());
        inventoryMapper.updateEnquiry(enquiry);
    }

    @Override
    public void applyRefundEnquiry(Integer loginUserId, InventorEnquiryUpdateDTO inventorEnquiryUpdateDTO) {
        Enquiry enquiry = inventoryMapper.selectEnquiryByEId(inventorEnquiryUpdateDTO.getId());//查询要货单
        if (enquiry == null){throw new BusinessException("要货单不存在");}
        if (!Objects.equals(enquiry.getApplicantId(), loginUserId)){throw new AuthorityException("权限不足");}
        if (!List.of(1,2,3,4,5).contains(enquiry.getState())){throw new BusinessException("当前状态无法申请退款");}
        if (inventorEnquiryUpdateDTO.getReqCause() == null){throw new BusinessException("请填写退款原因");}
        enquiry.setState(5);
        enquiry.setReqCause(inventorEnquiryUpdateDTO.getReqCause());
        if (inventorEnquiryUpdateDTO.getAttachment()!= null){ enquiry.setAttachment(inventorEnquiryUpdateDTO.getAttachment());}
        inventoryMapper.updateEnquiry(enquiry);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundEnquiry(Integer loginUserId, InventorEnquiryUpdateDTO inventorEnquiryUpdateDTO) {
        Enquiry enquiry = inventoryMapper.selectEnquiryByEId(inventorEnquiryUpdateDTO.getId());//查询要货单
        if (enquiry.getState()!=5){throw new BusinessException("当前要货单不处于退款中");}
        Outbound outbound = inventoryMapper.selectOutboundByEId(inventorEnquiryUpdateDTO.getId());//查询关联的出库单
        if (outbound == null){//未关联出库单
            Integer deptFatherId = deptService.selectDeptFatherIdById(enquiry.getDeptId());//获取提交要货单的部门的父部门id
            if (!deptService.getDeptIdsByUserId(loginUserId).contains(deptFatherId)){//判断当前用户所属部门是否为要货单的部门的父部门
                throw new AuthorityException("权限不足");
            }
        }else{//关联了出库单
            //判断当前用户是否为出库单的出库人,只允许出库人处理退款
            if (!outbound.getStockerId().equals(loginUserId)){throw new AuthorityException("权限不足");}
        }
        if (inventorEnquiryUpdateDTO.getHandle() == null){throw new BusinessException("请选择处理结果");}
        switch (inventorEnquiryUpdateDTO.getHandle()){
            case 0://驳回退款申请
                if (inventorEnquiryUpdateDTO.getRespCause() == null){throw new BusinessException("请填写驳回原因");}
                if (outbound == null) {//未关联出库单
                    enquiry.setState(1);
                }else{//关联了出库单
                    enquiry.setState(outbound.getState());
                }
                enquiry.setReqCause(inventorEnquiryUpdateDTO.getRespCause());
                break;
            case 1://同意退款申请
                //TODO 实现退款功能
                if (outbound != null) {//关联了出库单
                    if (outbound.getState()!=2){
                        //获取出库明细
                        List<InvOutDetailVO> details = inventoryMapper.selectODByTypeAndId(1, inventorEnquiryUpdateDTO.getId());
                        //重新将库存入库
                        details.forEach(detail -> inventoryExService.updateRepertory(1,outbound.getId(), outbound.getStoreOutId(), detail.getId(), 0, detail.getNum()));
                    }
                    //修改出库单状态为已取消
                    outbound.setState(5);
                    inventoryMapper.updateOutbound(outbound);
                }
                enquiry.setState(6);//修改要货单状态为已退款
                break;
            case 2://需要补充材料
                if (inventorEnquiryUpdateDTO.getRespCause() == null){throw new BusinessException("请填写回复");}
                break;
        }
        inventoryMapper.updateEnquiry(enquiry);
    }

    @Override
    public PageResult<InvEnqSimVO> selectEnquiryList(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, Integer no, Integer state, String dept, Integer minMoney, Integer maxMoney) {
        Integer applicantId = null;
        List<Integer> validDeptIds = null;
        switch (dataCoverage){
            case 2:
                applicantId = loginUserId;
                break;
            case 1:
                validDeptIds = deptService.getSonDeptIdsByUserId(loginUserId);//获取当前用户所属部门及其直属子部门的部门id列表
                break;
            case 0:
                break;
        }
        Integer total = inventoryMapper.countEnquiry(applicantId, validDeptIds, no, state, dept, minMoney, maxMoney);
        Integer skip = (page - 1) * pageSize;
        List<InvEnqSimVO> items = inventoryMapper.selectEnquiryList(skip, pageSize, applicantId, validDeptIds, no, state, dept, minMoney, maxMoney);
        return new PageResult<>(total, items);
    }

    @Override
    public InvEnqVO selectEnquiryByEId(Integer loginUserId, Integer dataCoverage, Integer id) {
        InvEnqVO invEnqVO = inventoryMapper.selectFullEnquiryByEId(id);
        if (invEnqVO == null){throw new BusinessException("要货单不存在");}
        switch (dataCoverage){
            case 2:
                if (!Objects.equals(invEnqVO.getApplicantId(), loginUserId)){throw new AuthorityException("权限不足");}
                break;
            case 1:
                if (!deptService.getSonDeptIdsByUserId(loginUserId).contains(invEnqVO.getDeptId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        List<InvEnqDetailVO> details = inventoryMapper.selectEDByEId(id);
        invEnqVO.setDetails(details);
        return invEnqVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOutbound(Integer loginUserId, Integer dataCoverage, InvOutDTO invOutDTO) {
        switch (dataCoverage){
            case 2:
                if (!inventoryMapper.selectStoreIdsByUserId(loginUserId).contains(invOutDTO.getStoreOutId())){throw new AuthorityException("权限不足");}
                break;
            case 1:
                if (!inventoryMapper.selectAllStoreIdsByUserId(loginUserId).contains(invOutDTO.getStoreOutId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        Outbound outbound = new Outbound();
        outbound.setNo(uniqueNo.getUniqueNo("IO"));
        outbound.setStockerId(loginUserId);
        outbound.setStoreOutId(invOutDTO.getStoreOutId());
        outbound.setState(2);
        outbound.setAttachment(invOutDTO.getAttachment());
        if (invOutDTO.getEnquiryId() == null){//未关联要货单
            //未关联要货单的出库单必须填写出库单明细
            if (invOutDTO.getDetails() == null){throw new BusinessException("请填写出库单明细");}
            //未关联要货单的出库单必须填写出库原因
            if (invOutDTO.getAttachment() == null || invOutDTO.getAttachment().isEmpty()){throw new BusinessException("请填写出库原因");}
            outbound.setStoreInId(null);
            //插入出库单
            inventoryMapper.insertOutbound(outbound);
            //插入出库明细单
            List<EnquiryDetail> details = invOutDTO.getDetails().stream().map(detail -> {
                EnquiryDetail enquiryDetail = new EnquiryDetail();
                BeanUtils.copyProperties(detail, enquiryDetail);
                enquiryDetail.setSourceType(0);
                enquiryDetail.setSourceId(outbound.getId());
                enquiryDetail.setMoney(BigDecimal.ZERO);
                return enquiryDetail;
            }).toList();
            inventoryMapper.batchInsertED(List.of("source_type","source_id","material_id", "num", "money", "remark"), details);
        }else{//关联了要货单
            Enquiry enquiry = inventoryMapper.selectEnquiryByEId(invOutDTO.getEnquiryId());//获取要货单
            if (enquiry == null){throw new BusinessException("要货单不存在");}
            if (enquiry.getState() != 1){throw new BusinessException("该要货单无法创建出库单");}
            //修改要货单状态为待出库
            enquiry.setState(2);
            inventoryMapper.updateEnquiry(enquiry);
            //插入出库单
            outbound.setEnquiryId(invOutDTO.getEnquiryId());
            outbound.setStoreInId(enquiry.getStoreId());
            inventoryMapper.insertOutbound(outbound);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)

    public void updateOutbound(Integer loginUserId, Integer dataCoverage, InvOutDTO invOutDTO) {
        switch (dataCoverage){
            case 2:
                if (!inventoryMapper.selectStoreIdsByUserId(loginUserId).contains(invOutDTO.getStoreOutId())){throw new AuthorityException("权限不足");}
                break;
            case 1:
                if (!inventoryMapper.selectAllStoreIdsByUserId(loginUserId).contains(invOutDTO.getStoreOutId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        Outbound outbound = inventoryMapper.selectOutboundByOId(invOutDTO.getId());
        if (outbound == null){throw new BusinessException("出库单不存在");}
        if (outbound.getState() != 2){throw new BusinessException("该出库单已出库无法修改");}
        if (!Objects.equals(outbound.getStockerId(), loginUserId)){throw new AuthorityException("权限不足");}
        outbound.setStoreOutId(invOutDTO.getStoreOutId());
        outbound.setAttachment(invOutDTO.getAttachment());
        inventoryMapper.updateOutbound(outbound);
        if (outbound.getEnquiryId()==null){
            //删除旧的出库单明细
            inventoryMapper.deleteEDByTypeAndId(0,invOutDTO.getId());
            //插入新的出库明细单
            List<EnquiryDetail> details = invOutDTO.getDetails().stream().map(detail -> {
                EnquiryDetail enquiryDetail = new EnquiryDetail();
                BeanUtils.copyProperties(detail, enquiryDetail);
                enquiryDetail.setSourceType(0);
                enquiryDetail.setSourceId(outbound.getId());
                enquiryDetail.setMoney(BigDecimal.ZERO);
                return enquiryDetail;
            }).toList();
            inventoryMapper.batchInsertED(List.of("source_type","source_id","material_id", "num", "money", "remark"), details);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOutboundToDelivery(Integer loginUserId, InvOutStateDTO invOutStateDTO) {
        Outbound outbound = inventoryMapper.selectOutboundByOId(invOutStateDTO.getId());
        List<InvOutDetailVO> details;//要货单明细
        if (outbound == null){throw new BusinessException("出库单不存在");}
        if (outbound.getState() != 2){throw new BusinessException("该出库单已出库");}
        if (!Objects.equals(outbound.getStockerId(), loginUserId)){throw new AuthorityException("权限不足");}
        outbound.setState(3);
        outbound.setAttachment(invOutStateDTO.getAttachment());
        inventoryMapper.updateOutbound(outbound);
        if (outbound.getEnquiryId() != null){//关联了要货单
            //修改要货单状态为待接收
            Enquiry enquiry = new Enquiry();
            enquiry.setState(3);
            inventoryMapper.updateEnquiry(enquiry);
            details = inventoryMapper.selectODByTypeAndId(1,outbound.getEnquiryId());
        }else{
            details = inventoryMapper.selectODByTypeAndId(0,invOutStateDTO.getId());
        }
        //扣减库存
        details.forEach(detail -> inventoryExService.updateRepertory(1,invOutStateDTO.getId(),outbound.getStoreOutId(),detail.getId(),1, detail.getNum()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOutboundToFinish(Integer loginUserId, InvOutStateDTO invOutStateDTO) {
        Outbound outbound = inventoryMapper.selectOutboundByOId(invOutStateDTO.getId());
        if (outbound == null){throw new BusinessException("出库单不存在");}
        if (outbound.getState() != 3){throw new BusinessException("该出库单状态不允许修改为已完成");}
        if (!Objects.equals(outbound.getStockerId(), loginUserId)){throw new AuthorityException("权限不足");}
        if(outbound.getAttachment() == null && invOutStateDTO.getAttachment() == null){throw new BusinessException("请填写送达凭证");}
        outbound.setState(4);
        if (invOutStateDTO.getAttachment() != null && !invOutStateDTO.getAttachment().isEmpty()){
            outbound.setAttachment(invOutStateDTO.getAttachment());
        }
        inventoryMapper.updateOutbound(outbound);
        if (outbound.getEnquiryId() != null){//关联了要货单
            //修改要货单状态为已完成
            Enquiry enquiry = new Enquiry();
            enquiry.setState(4);
            inventoryMapper.updateEnquiry(enquiry);
            //入库到目标仓库
            List<InvOutDetailVO> details = inventoryMapper.selectODByTypeAndId(1,outbound.getEnquiryId());
            details.forEach(detail -> inventoryExService.updateRepertory(1,outbound.getId(),outbound.getStoreInId(),detail.getId(),0, detail.getNum()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertStoreAmend(Integer loginUserId, Integer dataCoverage, InvAmendDTO invAmendDTO) {
        switch (dataCoverage){
            case 1:
                if (!inventoryMapper.selectStoreIdsByUserId(loginUserId).contains(invAmendDTO.getStoreId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        StoreAmend storeAmend = new StoreAmend();
        BeanUtils.copyProperties(invAmendDTO, storeAmend);
        storeAmend.setStoreId(invAmendDTO.getStoreId());
        storeAmend.setRemark(invAmendDTO.getRemark());
        storeAmend.setApplicantId(loginUserId);
        inventoryMapper.insertStoreAmend(storeAmend);//插入库存修正单基本信息
        List<StoreAmendDetail> repertoryList = new ArrayList<>();
        for (InvAmendDetailDTO material : invAmendDTO.getMaterials()) {
            StoreAmendDetail storeAmendDetail = new StoreAmendDetail();
            storeAmendDetail.setAmendId(storeAmend.getId());
            BeanUtils.copyProperties(material, storeAmendDetail);
            repertoryList.add(storeAmendDetail);
        }
        inventoryMapper.insertStoreAmendDetail(repertoryList);//插入库存修正单明细
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStoreAmendState(Integer loginUserId, Integer dataCoverage, InvAmendDTO invAmendDTO) {
        switch (dataCoverage){
            case 2:
                if (!inventoryMapper.selectSonStoreIdsByUserId(loginUserId).contains(invAmendDTO.getStoreId())){throw new AuthorityException("权限不足");}
                break;
            case 1:
                if (!inventoryMapper.selectAllStoreIdsByUserId(loginUserId).contains(invAmendDTO.getStoreId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        StoreAmend storeAmend = new StoreAmend();
        storeAmend.setId(invAmendDTO.getId());
        storeAmend.setState(invAmendDTO.getState());
        if (invAmendDTO.getState() != 1 && invAmendDTO.getState() != 2 ){throw new BusinessException("状态异常");}
        if (invAmendDTO.getState() == 2){//驳回
            if (invAmendDTO.getCause() == null){throw new BusinessException("请填写驳回原因");}
            storeAmend.setCause(invAmendDTO.getCause());
        } else {// 通过
            List<StoreAmendDetail> details = inventoryMapper.selectBaseAmendDetailByOId(invAmendDTO.getId());//获取库存修正单明细
            Integer updateCount = inventoryMapper.batchUpdateRepertoryByRID(details);//批量修改库存
            if (updateCount != details.size()){throw new BusinessException("库存修正失败,修正后存在负库存");}
        }
        inventoryMapper.updateStoreAmendState(storeAmend);
    }

    @Override
    public PageResult<InvAmendSimpleVO> selectStoreAmendList(Integer loginUserId, Integer dataCoverage, Integer page, Integer pageSize, Integer state, String dept) {
        List<Integer> validStoreIds = null;
        switch (dataCoverage){
            case 2:
                validStoreIds = inventoryMapper.selectStoreIdsByUserId(loginUserId);
                break;
            case 1:
                validStoreIds = inventoryMapper.selectAllStoreIdsByUserId(loginUserId);
                break;
            case 0:
                break;
        }
        Integer total = inventoryMapper.countStoreAmendList(validStoreIds, state, dept);
        Integer skip = (page - 1) * pageSize;
        List<InvAmendSimpleVO> items = inventoryMapper.selectStoreAmendList(validStoreIds, state, dept, skip, pageSize);
        return new PageResult<>(total, items);
    }

    @Override
    public InvAmendVO selectStoreAmendById(Integer loginUserId, Integer dataCoverage, Integer id) {
        InvAmendVO invAmendVO = inventoryMapper.selectStoreAmendById(id);
        if (invAmendVO == null){throw new BusinessException("库存修正单不存在");}
        switch (dataCoverage){
            case 2:
                if (!inventoryMapper.selectStoreIdsByUserId(loginUserId).contains(invAmendVO.getStoreId())){throw new AuthorityException("权限不足");}
                break;
            case 1:
                if (!inventoryMapper.selectAllStoreIdsByUserId(loginUserId).contains(invAmendVO.getStoreId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        invAmendVO.setDetails(inventoryMapper.selectAmendDetailByOId(id));
        return invAmendVO;
    }

    @Override
    public PageResult<InventoryStoreSimplerVO> selectStoreByUserId(Integer userId) {
        List<Integer> storeIds = this.selectStoreIdsByUserId(userId);//获取用户所属部门的仓库id列表
        List<InventoryStoreSimplerVO> items = inventoryMapper.selectStoreByIds(storeIds);//根据仓库id列表查询仓库信息
        return new PageResult<>(items.size(), items);
    }

    @Override
    public PageResult<InvOutSimVO> selectOutboundList(Integer loginUserId,
                                                      Integer dataCoverage,
                                                      Integer page,
                                                      Integer pageSize,
                                                      String no,
                                                      String stocker,
                                                      String store,
                                                      String dept,
                                                      Integer state) {
        List<Integer> validStoreIds = null;
        switch (dataCoverage){
            case 2:
                validStoreIds = inventoryMapper.selectStoreIdsByUserId(loginUserId);
                break;
            case 1:
                validStoreIds = inventoryMapper.selectAllStoreIdsByUserId(loginUserId);
                break;
            case 0:
                break;
        }
        Integer total = inventoryMapper.countOutbound(validStoreIds, no, stocker, store, dept, state);
        Integer skip = (page - 1) * pageSize;
        List<InvOutSimVO> items = inventoryMapper.selectOutboundList(validStoreIds, skip, pageSize, no, stocker, store, dept, state);
        return new PageResult<>(total, items);
    }

    @Override
    public InvOutVO selectOutboundByOId(Integer loginUserId, Integer dataCoverage, Integer id) {
        Outbound outbound = inventoryMapper.selectOutboundByOId(id);//获取出库单（用于权限校验）
        if (outbound == null){throw new BusinessException("出库单不存在");}
        switch (dataCoverage){
            case 2:
                if (!inventoryMapper.selectStoreIdsByUserId(loginUserId).contains(outbound.getStoreOutId())){throw new AuthorityException("权限不足");}
                break;
            case 1:
                if (!inventoryMapper.selectAllStoreIdsByUserId(loginUserId).contains(outbound.getStoreOutId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        InvOutVO invOutVO = inventoryMapper.selectFullOutboundByOId(id);
        List<InvOutDetailVO> details;
        if (invOutVO.getEnquiryId()!= null){
            details = inventoryMapper.selectODByTypeAndId(1,invOutVO.getEnquiryId());
        }else{
            details = inventoryMapper.selectODByTypeAndId(0,id);
        }
        invOutVO.setDetails(details);
        return invOutVO;
    }


}
