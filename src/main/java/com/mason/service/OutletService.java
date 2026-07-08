package com.mason.service;

import com.mason.domain.PageResult;
import com.mason.domain.dto.OutletGoodsDTO;
import com.mason.domain.dto.OutletStateDTO;
import com.mason.domain.vo.OutletGoodsVO;

public interface OutletService {
    /**
     * 根据门店id获取门店商品列表
     * @param loginUserId 登录用户id
     * @param dataCoverage 数据范围
     * @param id 门店id
     * @param page 页码
     * @param pageSize 每页数量
     * @param name 商品名称
     * @param className 分类名称
     * @param type 商品类型
     * @return 商品列表
     */
    PageResult<OutletGoodsVO> selectOutletGoodsList(Integer loginUserId, Integer dataCoverage, Integer id,Integer page, Integer pageSize, String name, String className, Integer type);

    /**
     * 修改门店商品状态
     * @param outletGoodsDTO 修改信息
     */
    void updateOutletGoodsState(Integer loginUserId,Integer dataCoverage,OutletGoodsDTO outletGoodsDTO);

    /**
     * 修改门店状态
     * @param loginUserId 登录用户id
     * @param dataCoverage 数据范围
     * @param outletStateDTO 修改信息
     **/
    void updateOutletState(Integer loginUserId, Integer dataCoverage, OutletStateDTO outletStateDTO);

}
