package com.mason.service.impl;

import com.mason.domain.PageResult;
import com.mason.domain.dto.OutletGoodsDTO;
import com.mason.domain.dto.OutletStateDTO;
import com.mason.domain.po.DeptGoods;
import com.mason.domain.vo.MenuGroupComboDishVO;
import com.mason.domain.vo.OutletGoodsVO;
import com.mason.exception.AuthorityException;
import com.mason.exception.BusinessException;
import com.mason.mapper.OutletMapper;
import com.mason.service.DeptService;
import com.mason.service.MenuService;
import com.mason.service.OutletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OutletServiceImpl implements OutletService {
    @Autowired
    private OutletMapper outletMapper;

    @Autowired
    private DeptService deptService;
    @Autowired
    private MenuService menuService;
    @Override
    public PageResult<OutletGoodsVO> selectOutletGoodsList(Integer loginUserId,
                                                           Integer dataCoverage,
                                                           Integer id,
                                                           Integer page,
                                                           Integer pageSize,
                                                           String name,
                                                           String className,
                                                           Integer type) {
        switch (dataCoverage){
            case 1:
                if (!deptService.getDeptIdsByUserId(loginUserId).contains(id)){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        Integer total = outletMapper.countOutletGoodsList(id,name,className,type);
        if (total == 0){return new PageResult<>(0,null);}
        Integer skip = (page - 1) * pageSize;
        List<OutletGoodsVO> items = outletMapper.selectOutletGoodsList(id,skip,pageSize,name,className,type);
        return new PageResult<>(total,items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOutletGoodsState(Integer loginUserId,Integer dataCoverage,OutletGoodsDTO outletGoodsDTO) {
        switch (dataCoverage){
            case 1:
                if (!deptService.getDeptIdsByUserId(loginUserId).contains(outletGoodsDTO.getId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        //获取门店售罄的商品列表
        List<DeptGoods> deptGoodsList = outletMapper.selectOutletDeptGoods(outletGoodsDTO.getId());// 查询门售罄的商品信息
        //转换为map集合map<商品类型,售罄商品集合>
        Map<Integer, List<Integer>> goodsTypeMap = deptGoodsList.stream()
                .collect(Collectors.groupingBy(DeptGoods::getGoodsType, Collectors.mapping(DeptGoods::getGoodsId,Collectors.toList())));
        goodsTypeMap.computeIfAbsent(0,value->new ArrayList<>());//空值保护
        goodsTypeMap.computeIfAbsent(1,value->new ArrayList<>());//空值保护
        DeptGoods deptGoods = new DeptGoods(outletGoodsDTO.getId(),outletGoodsDTO.getType(),outletGoodsDTO.getGoodsId(),outletGoodsDTO.getState(),null,null);
        if (outletGoodsDTO.getState()){//商品状态设置为正常
            if (outletGoodsDTO.getType() == 0){//设置的商品为菜品
                if (goodsTypeMap.get(0).contains(outletGoodsDTO.getGoodsId())){//该菜品处于售罄状态
                    outletMapper.deleteOutletDeptGoods(deptGoods);
                }
            }else{//设置的商品为套餐
                if (goodsTypeMap.get(1).contains(outletGoodsDTO.getGoodsId())){//该套餐处于售罄状态
                    //判断该套餐下分组中的菜品是否全部售罄,如果全部售罄则不能修改为正常
                    //获取该套餐下的所有分组信息（已被套餐使用的分组）
                    List<MenuGroupComboDishVO> menuGroupComboDishVOS = menuService.selectMenuComboGroupDish(null, outletGoodsDTO.getGoodsId(), null);
                    for (MenuGroupComboDishVO menuGroupComboDishVO : menuGroupComboDishVOS) {
                        if (new HashSet<>(goodsTypeMap.get(0)).containsAll(menuGroupComboDishVO.getDishIds())){//某个分组中的所有菜品均售罄
                            throw new BusinessException("当前套餐下存在不可用分组");
                        }
                    }
                    outletMapper.deleteOutletDeptGoods(deptGoods);
                }
            }
        }else{//商品状态设置为售罄
            if (outletGoodsDTO.getType() == 0){//设置的商品为菜品
                if (!goodsTypeMap.get(0).contains(outletGoodsDTO.getGoodsId())){//该菜品处于正常状态
                    goodsTypeMap.get(0).add(outletGoodsDTO.getGoodsId());//将当前菜品临时加入售罄菜品列表，方便后面判断
                    //判断使用该菜品的分组中的所有菜品是否均售罄,如果全部售罄则需要将使用了该分组的套餐全部设置为售罄
                    //获取包含该菜品的所有分组信息（已被套餐使用的分组）
                    List<MenuGroupComboDishVO> menuGroupComboDishVOS = menuService.selectMenuComboGroupDish(null, null, outletGoodsDTO.getGoodsId());
                    for (MenuGroupComboDishVO menuGroupComboDishVO : menuGroupComboDishVOS) {
                        if (new HashSet<>(goodsTypeMap.get(0)).containsAll(menuGroupComboDishVO.getDishIds())){//某个分组中的所有菜品均售罄
                            for (Integer comboId : menuGroupComboDishVO.getComboIds()) {//递归将使用了该分组的套餐设置为售罄
                                updateOutletGoodsState(loginUserId,dataCoverage,new OutletGoodsDTO(outletGoodsDTO.getId(),1,comboId,false));
                            }
                        }
                    }
                    outletMapper.insertOutletDeptGoods(outletGoodsDTO);//将菜品加入售罄表
                }
            }else{//设置的商品为套餐
                if (!goodsTypeMap.get(1).contains(outletGoodsDTO.getGoodsId())){//该套餐处于正常状态
                    outletMapper.insertOutletDeptGoods(outletGoodsDTO);
                }
            }
        }
    }

    @Override
    public void updateOutletState(Integer loginUserId, Integer dataCoverage, OutletStateDTO outletStateDTO) {
        switch (dataCoverage){
            case 1:
                if (!deptService.getDeptIdsByUserId(loginUserId).contains(outletStateDTO.getId())){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        outletMapper.updateOutletState(outletStateDTO);
    }
}
