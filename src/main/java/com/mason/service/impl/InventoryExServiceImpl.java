package com.mason.service.impl;

import com.mason.domain.dto.InvLogDTO;
import com.mason.domain.dto.InvMaterialNumDTO;
import com.mason.domain.dto.InventoryRepertoryDTO;
import com.mason.domain.po.StoreMaterial;
import com.mason.exception.BusinessException;
import com.mason.mapper.InventoryMapper;
import com.mason.service.InventoryExService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryExServiceImpl implements InventoryExService {
    @Autowired
    private InventoryMapper inventoryMapper;

    //内部方法,修改库存
    private void innUpdateRepertory(Integer sourceType,Integer sourceId, Integer storeId, Integer materialId, Integer operation, Integer num){
        //找到对应的库存
        StoreMaterial repertory = inventoryMapper.selectRepertoryBySIdAndMId(storeId, materialId);
        if (repertory == null){//没有该库存
            if (operation == 1){throw new BusinessException("库存不足");}
            InventoryRepertoryDTO inventoryRepertoryDTO = new InventoryRepertoryDTO(null, storeId, materialId, num);
            //插入库存
            inventoryMapper.insertRepertory(inventoryRepertoryDTO);
            //插入库存日志
            inventoryMapper.insertRepertoryLog(inventoryRepertoryDTO.getId(), operation, num, sourceId,sourceType);
        }else{
            if (operation == 1){//出库操作
                //判断库存是否充足
                if (repertory.getSum() < num){throw new BusinessException("库存不足");}
                //修改库存
                Integer sum = repertory.getSum() - num;
                inventoryMapper.updateRepertory(repertory.getId(), sum);
                //插入库存日志
                inventoryMapper.insertRepertoryLog(repertory.getId(), operation, num, sourceId,sourceType);
            }else{//入库操作
                //修改库存
                Integer sum = repertory.getSum() + num;
                inventoryMapper.updateRepertory(repertory.getId(), sum);
                //插入库存日志
                inventoryMapper.insertRepertoryLog(repertory.getId(), operation, num, sourceId,sourceType);
            }
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRepertory(Integer sourceType,Integer sourceId, Integer storeId, Integer materialId, Integer operation, Integer num) {
        innUpdateRepertory(sourceType,sourceId, storeId, materialId, operation, num);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackRepertory(Integer sourceType, List<Integer> sourceIds) {
        //根据来源id和类型查询库存出库日志
        List<InvLogDTO> logs = inventoryMapper.selectLogListByIds(sourceType, sourceIds, 1, null, null);
        logs.forEach(log-> log.setOperation(0));//设置为入库操作
        //按照原料id进行分组求和
        Map<String, Integer> materialSumMap = logs
                .stream()
                .collect(Collectors.groupingBy(item->item.getStoreId() + "_" + item.getMaterialId(), Collectors.summingInt(InvLogDTO::getNum)));
        //转回list数组
        List<InvMaterialNumDTO> materialNumList = materialSumMap.entrySet()
                .stream()
                .map(entry -> {
                    String[] ids = entry.getKey().split("_");
                    return new InvMaterialNumDTO(Integer.parseInt(ids[0]), Integer.parseInt(ids[1]), entry.getValue());
                })
                .toList();
        inventoryMapper.batchUpdateRepertory(materialNumList);//批量回滚库存
        inventoryMapper.batchInsertLog(logs);//批量添加日志
    }
}
