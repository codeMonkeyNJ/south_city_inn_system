package com.mason.mapper;

import com.mason.domain.dto.OutletGoodsDTO;
import com.mason.domain.dto.OutletStateDTO;
import com.mason.domain.po.DeptGoods;
import com.mason.domain.vo.OutletGoodsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OutletMapper {

    /**
     * 统计门店商品数量
     * @param id 门店id
     * @param name 商品名称
     * @param className 分类名称
     * @param type 商品类型
     * @return 商品数量
     */
    Integer countOutletGoodsList(Integer id, String name, String className, Integer type);

    /**
     * 查询门店商品列表
     * @param id 门店id
     * @param skip 跳过数量
     * @param pageSize 页大小
     * @param name 模糊查询商品名称
     * @param className 分类名称
     * @param type 0:普通商品 1:服务商品
     * @return 门店商品列表
     */
    List<OutletGoodsVO> selectOutletGoodsList(Integer id,Integer skip, Integer pageSize, String name, String className, Integer type);

    /**
     * 查询门店商品是否售罄
     * @param deptId 门店id
     * @return 0:未售罄 1:售罄
     */
    @Select("select dept_id, goods_type, goods_id, state from dept_goods where dept_id = #{deptId}")
    List<DeptGoods> selectOutletDeptGoods(Integer deptId);

    /**
     * 删除门店商品售罄信息
     * @param deptGoods 门店商品售罄信息
     */
    @Select("delete from dept_goods where dept_id = #{deptId} and goods_id = #{goodsId} and goods_type = #{goodsType}")
    void deleteOutletDeptGoods(DeptGoods deptGoods);

    /**
     * 添加门店商品售罄信息
     * @param outletGoodsDTO 门店商品售罄信息
     */
    @Select("insert into dept_goods(dept_id, goods_type, goods_id, state) values(#{id}, #{type}, #{goodsId}, #{state})")
    void insertOutletDeptGoods(OutletGoodsDTO outletGoodsDTO);

    /**
     * 修改门店营业状态
     * @param outletStateDTO 营业状态信息
     */
    @Select("update dept set state = #{state} where id = #{id}")
    void updateOutletState(OutletStateDTO outletStateDTO);
}
