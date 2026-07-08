package com.mason.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mason.domain.PageResult;
import com.mason.domain.dto.MaterialCarDTO;
import com.mason.domain.po.Material;
import com.mason.domain.vo.*;

import java.util.List;

public interface MaterialService {
    /**
     * 添加原料分类
     * @param name 原料分类名称
     */
    void insertMaterialClass(String name);

    /**
     * 修改原料分类
     * @param id 原料分类id
     * @param name 原料分类名称
     */
    void updateMaterialClass(Integer id, String name);

    /**
     * 批量删除原料分类
     * @param ids 原料分类id列表
     */
    void batchDeleteMaterialClass(List<Integer> ids);

    /**
     * 获取所有原料分类
     * @param page 页码
     * @param pageSize 页大小
     * @return 原料分类列表
     */
    PageResult<MaterialClassVO> selectMaterialClass(Integer page, Integer pageSize);

    /**
     * 添加原料
     * @param material 原料信息
     */
    void insertMaterial(Material material);

    /**
     * 修改原料
     * @param material 原料信息
     */
    void updateMaterial(Material material);

    /**
     * 获取所有原料
     * @param page 页码
     * @param pageSize 页大小
     * @param name 原料名称
     * @param classId 原料分类id
     * @param state 原料状态
     * @return 原料列表
     */
    PageResult<MaterialListVO> selectMaterial(Integer page, Integer pageSize, String name, String classId,Integer state);

    /**
     * 根据id查询原料
     * @param id 原料id
     * @return 原料信息
     */
    MaterialVO selectMaterialByMaterialId(Integer id);

    /**
     * 根据采购申请id获取原料列表
     */
    List<PurchaseApplyDetailVO> selectMaterialByPAId(Integer purchaseApplyId);

    /**
     * 根据供应商id获取原料列表
     */
    List<PurchaseSupplierMaterialVO> selectMaterialBySupplierId(Integer supplierId);


    /**
     * 修改原料购物车
     **/
    void updateMaterialCar(Integer loginUserId, MaterialCarDTO materialCarDTO) throws JsonProcessingException;

    /**
     * 获取原料购物车
     **/
    MaterialCarVO selectMaterialCar(Integer loginUserId) throws JsonProcessingException;

    /**
     * 根据原料id列表批量获取原料信息
     * @param fields 需要的字段
     * @param materialIds 原料id列表
     */
    List<Material> selectMListByMIds(List<String> fields, List<Integer> materialIds);

}
