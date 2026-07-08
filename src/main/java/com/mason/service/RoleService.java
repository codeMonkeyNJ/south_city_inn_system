package com.mason.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mason.domain.PageResult;
import com.mason.domain.dto.AuthDTO;
import com.mason.domain.dto.RoleDTO;
import com.mason.domain.vo.AuthVO;
import com.mason.domain.vo.RoleAuthVO;
import com.mason.domain.vo.RoleSimpleVO;
import com.mason.domain.vo.RoleVO;

import java.util.List;

public interface RoleService {
    /**
     * 获取所有权限信息
     */
    PageResult<AuthVO> getAllPermission(Integer page, Integer pageSize,String name);

    /**
     * 获取用户权限列表
     */
    List<RoleAuthVO> getPermissionList(Integer userId);

    /**
     * 获取用户权限
     */
    RoleAuthVO getPermission(Integer userId, String authCode) throws JsonProcessingException;

    /**
     * 添加角色
     */
    void addRole(RoleDTO roleDTO);


    /**
     * 添加权限
     */
    void addPermission(AuthDTO authDTO);

    /**
     * 修改权限
     */
    void updatePermission(AuthDTO authDTO);


    /**
     * 批量删除权限
     * @param ids 权限id列表
     */
    void batchDeletePermission(List<Integer> ids);

    /**
     * 修改角色
     */
    void updateRole(RoleDTO roleDTO);

    /**
     * 查询所有角色
     */
    PageResult<RoleSimpleVO> getAllRole(Integer page, Integer pageSize, String roleName);

    /**
     * 根据id查询角色
     */
    RoleVO getRoleById(Integer id);

    /**
     * 批量删除角色
     */
    void batchDeleteRole(List<Integer> ids);

    /**
     * 根据用户id查询角色
     * @param id 用户id
     * @return 角色名称列表
     */
    List<RoleDTO> selectRoleNameByUserId(Integer id);



}
