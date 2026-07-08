package com.mason.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.dto.MaterialCarDTO;
import com.mason.domain.po.Material;
import com.mason.domain.vo.MaterialCarVO;
import com.mason.domain.vo.MaterialClassVO;
import com.mason.domain.vo.MaterialListVO;
import com.mason.domain.vo.MaterialVO;
import com.mason.service.MaterialService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/material")
public class MaterialController {
    @Autowired
    private MaterialService materialService;
    /**
     * 获取原料分类列表
     */
    @GetMapping("/class")
    @AuthCode("material-class-select")
    public Result selectMaterialClass(Integer page, Integer pageSize){
        PageResult<MaterialClassVO> pageResult = materialService.selectMaterialClass(page,pageSize);
        return Result.success(pageResult);
    }
    /**
     * 添加原料分类
     */
    @PostMapping("/class")
    @AuthCode("material-class-insert")
    public Result insertMaterialClass(String name){
        materialService.insertMaterialClass(name);
        return Result.success();
    }
    /**
     * 修改原料分类
     */
    @PutMapping("/class")
    @AuthCode("material-class-update")
    public Result updateMaterialClass(Integer id,String name){
        materialService.updateMaterialClass(id,name);
        return Result.success();
    }
    /**
     * 删除原料分类
     */
    @DeleteMapping("/class")
    @AuthCode("material-class-delete")
    public Result deleteMaterialClass(@RequestParam List<Integer> ids){
        materialService.batchDeleteMaterialClass(ids);
        return Result.success();
    }

    /**
     * 获取原料列表
     */
    @GetMapping
    @AuthCode("material-select")
    public Result selectMaterial(Integer page, Integer pageSize, String name, String className,Integer state){
        PageResult<MaterialListVO> pageResult = materialService.selectMaterial(page,pageSize,name,className,state);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询原料
     */
    @GetMapping("/{id}")
    @AuthCode("material-select-id")
    public Result getMaterialById(@PathVariable Integer id){
        MaterialVO material = materialService.selectMaterialByMaterialId(id);
        return Result.success(material);
    }

    /**
     * 添加原料
     */
    @PostMapping
    @AuthCode("material-insert")
    public Result insertMaterial(@RequestBody Material material){
        materialService.insertMaterial(material);
        return Result.success();
    }
    /**
     * 修改原料
     */
    @PutMapping
    @AuthCode("material-update")
    public Result updateMaterial(@RequestBody Material material){
        materialService.updateMaterial(material);
        return Result.success();
    }
    /**
     * 修改原料购物车
     * 权限码：material-car-update
     */
    @PutMapping("/car")
    @AuthCode("material-car-update")
    public Result updateMaterialCar(HttpServletRequest request, @RequestBody MaterialCarDTO materialCarDTO) throws JsonProcessingException {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        materialService.updateMaterialCar(loginUserId,materialCarDTO);
        return Result.success();
    }

    /**
     * 根据用户id查询原料购物车
     * 权限码：material-car-select
     */
    @GetMapping("/car")
    @AuthCode("material-car-select")
    public Result selectMaterialCar(HttpServletRequest request) throws JsonProcessingException {
        Integer loginUserId = (Integer) request.getAttribute("loginUserId");
        MaterialCarVO materialCarVO = materialService.selectMaterialCar(loginUserId);
        return Result.success(materialCarVO);
    }
}
