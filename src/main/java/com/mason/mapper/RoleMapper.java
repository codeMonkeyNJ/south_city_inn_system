package com.mason.mapper;

import com.mason.domain.dto.AuthDTO;
import com.mason.domain.dto.RoleDTO;
import com.mason.domain.po.RoleAuth;
import com.mason.domain.po.Role;
import com.mason.domain.vo.AuthVO;
import com.mason.domain.vo.RoleAuthVO;
import com.mason.domain.vo.RoleSimpleVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper {
    /**
     * 统计权限总数
     */
    Integer countPermission(String name);

    /**
     * 获取所有权限信息
     */
    List<AuthVO> getAllPermission(Integer skip, Integer pageSize,String name);

    /**
     * 添加角色
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into role(name, `desc`) values(#{name}, #{desc})")
    void addRole(Role role);

    /**
     * 添加角色权限关系
     */
    void addRolePermission(List<RoleAuth> permissions);

    /**
     * 添加权限
     */
    @Insert("insert into auth(name, code, sort) values(#{name}, #{code}, #{sort})")
    void insertPermission(AuthDTO authDTO);

    /**
     * 修改权限
     */
    @Update("update auth set name = #{name}, code = #{code}, sort = #{sort} where id = #{id}")
    void updatePermission(AuthDTO authDTO);

    /**
     * 修改角色
     */
    @Update("update role set name = #{name}, `desc` = #{desc} where id = #{id}")
    void updateRole(Role role);

    /**
     * 删除角色权限关系
     */
    @Delete("delete from role_auth where role_id = #{id}")
    void deleteRolePermission(Integer id);
    
    /**
     * 统计角色总数
     */
    Integer countRole(String roleName);

    /**
     * 获取所有角色信息
     */
    List<RoleSimpleVO> getAllRole(Integer skip, Integer pageSize, String roleName);

    /**
     * 根据id查询角色信息
     */
    @Select("select id, name, `desc` from role where id = #{id}")
    Role getRoleById(Integer id);

    /**
     * 根据角色id查询角色拥有的权限信息
     * @param id 角色id
     * @return 权限信息
     */
    @Select("""
            select a.id,ra.dataCoverage,a.name,a.code from role r
                join role_auth ra on r.id = ra.role_id
                join auth a on a.id = ra.auth_id
                where r.id = #{id};
            """)
    List<RoleAuthVO> getRoleAuth(Integer id);

    /**
     * 批量删除角色权限关系
     */
    void batchDeleteRoleAuth(List<Integer> roleIds,List<Integer> authIds);

    /**
     * 批量删除角色
     */
    void batchDeleteRole(List<Integer> ids);


    /**
     * 根据用户id查询用户拥有的权限信息
     * @param userId 用户id
     * @return 权限信息
     */
    @Select("""
            select code,dataCoverage from user_role ur
                join role_auth ra on ur.role_id = ra.role_id
                join auth a on ra.auth_id = a.id
            where ur.user_id = #{userId};""")
    List<RoleAuthVO> getAuthByUserId(Integer userId);

    /**
     * 根据用户id查询角色
     * @param userId 用户id
     * @return 用户角色列表
     */
    @Select("select r.name,r.id from user_role ur join role r on ur.role_id=r.id where ur.user_id= #{userId}")
    List<RoleDTO> selectRoleNameByUserId(Integer userId);

    /**
     * 根据角色id查询对应的用户id列表
     * @param id 角色id
     * @return 用户id列表
     */
    @Select("select user_id from user_role where role_id = #{id}")
    List<Integer> selectUserIdsByRoleId(Integer id);

    /**
     * 批量删除权限
     */
    void batchDeletePermission(List<Integer> ids);

    /**
     * 批量删除用户角色关系
     */
    void batchDeleteUserRole(List<Integer> userIds,List<Integer> roleIds);
}
