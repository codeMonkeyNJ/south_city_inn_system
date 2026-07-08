package com.mason.controller;

import com.mason.anno.AuthCode;
import com.mason.domain.PageResult;
import com.mason.domain.Result;
import com.mason.domain.dto.AuthDTO;
import com.mason.domain.dto.RoleDTO;
import com.mason.domain.vo.AuthVO;
import com.mason.domain.vo.RoleSimpleVO;
import com.mason.domain.vo.RoleVO;
import com.mason.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 获取所有权限信息
     * @param page 页码
     * @param pageSize 每页显示数量
     */
    @AuthCode("auth-select")
    @GetMapping("/permission")
    public Result getAllPermission(Integer page, Integer pageSize,String name) {
        PageResult<AuthVO> pageResult = roleService.getAllPermission(page, pageSize,name);
        return Result.success(pageResult);
    }

    /**
     * 添加权限
     */
    @AuthCode("auth-insert")
    @PostMapping("/permission")
    public Result addPermission(@RequestBody AuthDTO authDTO) {
        roleService.addPermission(authDTO);
        return Result.success();
    }

    /**
     * 修改权限
     */
    @AuthCode("auth-update")
    @PutMapping("/permission")
    public Result updatePermission(@RequestBody AuthDTO authDTO) {
        roleService.updatePermission(authDTO);
        return Result.success();
    }

    /**
     * 批量删除权限
     */
    @AuthCode("auth-delete")
    @DeleteMapping("/permission")
    public Result deletePermission(@RequestParam List<Integer> ids) {
        roleService.batchDeletePermission(ids);
        return Result.success();
    }

    /**
     * 添加角色
     */
    @AuthCode("role-insert")
    @PostMapping
    public Result addRole(@RequestBody RoleDTO roleDTO) {
        roleService.addRole(roleDTO);
        return Result.success();
    }

    /**
     * 修改角色
     */
    @AuthCode("role-update")
    @PutMapping
    public Result updateRole(@RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleDTO);
        return Result.success();
    }

    /**
     * 查询所有角色
     */
    @AuthCode("role-select")
    @GetMapping
    public Result getAllRole(Integer page, Integer pageSize, String roleName) {
        PageResult<RoleSimpleVO> pageResult = roleService.getAllRole(page, pageSize, roleName);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询角色
     */
    @AuthCode("role-select-id")
    @GetMapping("/{id}")
    public Result getRoleById(@PathVariable Integer id) {
        RoleVO roleVO = roleService.getRoleById(id);
        return Result.success(roleVO);
    }

    /**
     * 批量删除角色
     */
    @AuthCode("role-delete")
    @DeleteMapping
    public Result deleteRole(@RequestParam List<Integer> ids) {
        roleService.batchDeleteRole(ids);
        return Result.success();
    }
}
