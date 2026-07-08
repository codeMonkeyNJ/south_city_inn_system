package com.mason.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mason.domain.PageResult;
import com.mason.domain.dto.MaterialCarDTO;
import com.mason.domain.dto.MaterialCarItemDTO;
import com.mason.domain.po.Material;
import com.mason.domain.vo.*;
import com.mason.mapper.MaterialMapper;
import com.mason.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MaterialServiceImpl implements MaterialService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MaterialMapper materialMapper;
    @Override
    public void insertMaterialClass(String name) {
        materialMapper.insertMaterialClass(name);
    }

    @Override
    public void updateMaterialClass(Integer id, String name) {
        materialMapper.updateMaterialClass(id, name);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteMaterialClass(List<Integer> ids) {
        materialMapper.updateMaterialClassIds2Null(ids);//将被删除的原料分类下的原料的分类id修改为null
        materialMapper.batchDeleteMaterialClass(ids);//删除原料分类
    }

    @Override
    public PageResult<MaterialClassVO> selectMaterialClass(Integer page, Integer pageSize) {
        Integer total = materialMapper.countMaterialClass();//统计总记录数
        Integer skip = (page - 1) * pageSize;
        List<MaterialClassVO> items = materialMapper.selectMaterialClass(skip, pageSize);//查询当前页数据
        return new PageResult<>(total, items);
    }

    @Override
    public void insertMaterial(Material material) {
        materialMapper.insertMaterial(material);
    }

    @Override
    public void updateMaterial(Material material) {
        materialMapper.updateMaterial(material);
    }

    @Override
    public PageResult<MaterialListVO> selectMaterial(Integer page, Integer pageSize, String name, String className,Integer state) {
        Integer total = materialMapper.countMaterial(name,className,state);//统计总记录数
        Integer skip = (page - 1) * pageSize;//跳过记录数
        List<MaterialListVO> items = materialMapper.selectMaterial(skip, pageSize, name, className,state);//查询当前页数据
        return new PageResult<>(total, items);
    }

    @Override
    public MaterialVO selectMaterialByMaterialId(Integer id) {
        return materialMapper.selectMaterialByMaterialId(id);
    }

    @Override
    public List<PurchaseApplyDetailVO> selectMaterialByPAId(Integer purchaseApplyId) {
        return materialMapper.selectMaterialByPAId(purchaseApplyId);
    }

    @Override
    public List<PurchaseSupplierMaterialVO> selectMaterialBySupplierId(Integer supplierId) {
        return materialMapper.selectMaterialBySupplierId(supplierId);
    }

    @Override
    public void updateMaterialCar(Integer loginUserId, MaterialCarDTO materialCarDTO) throws JsonProcessingException {
        if (materialCarDTO.getItems() == null || materialCarDTO.getItems().isEmpty()){//购物车为空
            stringRedisTemplate.delete("sys:material:Car:" + loginUserId);//删除购物车
        }
        //购物车不为空，则将购物车数据保存到redis中
        stringRedisTemplate.opsForValue().set("sys:material:Car:" + loginUserId, objectMapper.writeValueAsString(materialCarDTO.getItems()),7, TimeUnit.DAYS);
    }

    @Override
    public MaterialCarVO selectMaterialCar(Integer loginUserId) throws JsonProcessingException {
        String strCarItems = stringRedisTemplate.opsForValue().get("sys:material:Car:" + loginUserId);
        if(strCarItems == null){//Redis中没有该用户的购物车数据
            return new MaterialCarVO(new BigDecimal(0), new ArrayList<>());
        }
        List<MaterialCarItemDTO> CarItems= objectMapper.readValue(strCarItems, new TypeReference<>() {
        });
        if(CarItems == null || CarItems.isEmpty()){//Redis中没有该用户的购物车数据
            return new MaterialCarVO(new BigDecimal(0), new ArrayList<>());
        }
        List<Integer> materialIds = CarItems.stream().map(MaterialCarItemDTO::getMaterialId).toList();
        List<Material> materials = materialMapper.selectMIdMNMMByMIds(materialIds);//批量查询物料的名称和价格
        //将查询结果转为Map集合key为原料id，方便后续查询
        Map<Integer, Material> materialMap = materials.stream().collect(Collectors.toMap(Material::getId, Function.identity()));
        //构建购物车项
        List<MaterialCarItemVO> items = CarItems.stream().map(item -> {
            MaterialCarItemVO materialCarItemVO = new MaterialCarItemVO();
            materialCarItemVO.setMaterialId(item.getMaterialId());
            materialCarItemVO.setName(materialMap.get(item.getMaterialId()).getName());
            materialCarItemVO.setNum(item.getNum());
            materialCarItemVO.setMoney(materialMap.get(item.getMaterialId()).getPrice() * item.getNum());
            return materialCarItemVO;
        }).toList();
        //计算总金额
        BigDecimal amount = BigDecimal.ZERO;
        for (MaterialCarItemVO item : items) {
            amount = amount.add(BigDecimal.valueOf(item.getMoney()));
        }
        return new MaterialCarVO(amount, items);
    }

    @Override
    public List<Material> selectMListByMIds(List<String> fields, List<Integer> materialIds) {
        return materialMapper.selectMListByMIds(fields, materialIds);
    }


}
