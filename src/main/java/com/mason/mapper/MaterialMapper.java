package com.mason.mapper;

import com.mason.domain.po.Material;
import com.mason.domain.vo.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MaterialMapper {
    /**
     * 添加原料分类
     */
    @Insert("insert into material_class(name) values(#{name})")
    void insertMaterialClass(String name);

    /**
     * 修改原料分类
     */
    @Update("update material_class set name= #{name} where id= #{id}")
    void updateMaterialClass(Integer id, String name);

    /**
     * 批量删除原料分类
     */
    void batchDeleteMaterialClass(List<Integer> ids);

    /**
     * 统计原料分类总数
     */
    @Select("select count(*) from material_class")
    Integer countMaterialClass();

    /**
     * 获取原料分类列表
     */
    @Select("select id, name from material_class limit #{skip}, #{pageSize}")
    List<MaterialClassVO> selectMaterialClass(Integer skip, Integer pageSize);

    /**
     * 添加原料
     */
    void insertMaterial(Material material);

    /**
     * 修改原料
     */
    void updateMaterial(Material material);

    /**
     * 将被删除的原料分类下的原料的分类id修改为null
     * @param ids 被删除的原料分类id列表
     */
    void updateMaterialClassIds2Null(List<Integer> ids);

    /**
     * 统计原料数量
     * @param name 原料名称(可选)
     * @param className 原料分类(可选)
     */
    Integer countMaterial(String name, String className,Integer state);

    /**
     * 获取原料列表
     * @param name 原料名称(可选)
     * @param className 原料分类(可选)
     */
    List<MaterialListVO> selectMaterial(Integer skip, Integer pageSize, String name, String className,Integer state);

    /**
     * 根据id查询原料
     */
    @Select("""
        select m.cover, m.name, m.class_id as classId, mc.name as className, num, unit, pack, price, state, remark\s
        from material m left join material_class mc on m.class_id = mc.id
        where m.id = #{id}
    """)
    MaterialVO selectMaterialByMaterialId(Integer id);

    /**
     * 根据id列表查询原料列表
     */
    List<MaterialVO> selectMaterialByMaterialIds(Integer page, Integer pageSize, List<Integer> materialIds, String name, Integer classId);

    /**
     * 根据采购申请单id查询采购申请单的采购明细
     */
    @Select("""
        select pd.material_id,m.name,pd.plan_num,m.pack as unit
        from purchase_detail pd
        join material m on pd.material_id = m.id
        where pd.source_type = 1 and pd.source_id = #{purchaseApplyId}
    """)
    List<PurchaseApplyDetailVO> selectMaterialByPAId(Integer purchaseApplyId);

    /**
     * 根据供应商id查询供应商提供的物料列表
     */
    @Select("""
        select m.id,m.name
        from material m join supplier_material sm on m.id = sm.material_id
        where sm.supplier_id = #{supplierId}
    """)
    List<PurchaseSupplierMaterialVO> selectMaterialBySupplierId(Integer supplierId);

    /**
     * 根据物料id列表查询原料id、名称和价格
     */
    List<Material> selectMIdMNMMByMIds(List<Integer> materialIds);

    /**
     * 根据字段列表和id列表查询原料信息
     */
    List<Material> selectMListByMIds(List<String> fields, List<Integer> materialIds);
}
