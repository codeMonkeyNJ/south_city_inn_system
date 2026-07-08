package com.mason.service;

import java.util.List;

public interface InventoryExService {
    /**
     * 修改仓库库存
     * @param sourceType 来源类型(0:采购订单,1:出库单,2:商品订单)
     * @param sourceId 来源id(采购订单id/出库单id)
     * @param storeId 仓库id
     * @param materialId 物料id
     * @param operation 操作类型(0:入库,1:出库)
     * @param num 操作数量
     */
    void updateRepertory(Integer sourceType,Integer sourceId, Integer storeId, Integer materialId, Integer operation, Integer num);

    /**
     * 库存回滚
     * @param sourceType 来源类型(0:采购订单,1:出库单,2:商品订单)
     * @param sourceIds 来源ids(采购订单id/出库单id)
     */
    void rollbackRepertory(Integer sourceType,List<Integer> sourceIds);

}
