package com.mason.service.impl;

import com.mason.domain.PageResult;
import com.mason.domain.dto.AuthDTO;
import com.mason.domain.dto.RoleDTO;
import com.mason.domain.po.RoleAuth;
import com.mason.domain.po.Role;
import com.mason.domain.vo.AuthVO;
import com.mason.domain.vo.RoleAuthVO;
import com.mason.domain.vo.RoleSimpleVO;
import com.mason.domain.vo.RoleVO;
import com.mason.exception.BusinessException;
import com.mason.mapper.RoleMapper;
import com.mason.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult<AuthVO> getAllPermission(Integer page, Integer pageSize,String name) {

        Integer total = roleMapper.countPermission(name);//获取权限总数
        Integer skip = (page-1)*pageSize;
        List<AuthVO> items = roleMapper.getAllPermission(skip, pageSize,name);//获取权限列表
        return new PageResult<>(total, items);
    }

    @Override
    public List<RoleAuthVO> getPermissionList(Integer userId){
        List<RoleAuthVO> roleAuthList = roleMapper.getAuthByUserId(userId);//获取用户权限列表
        Map<String, String> roleAuthMap;
        if (!CollectionUtils.isEmpty(roleAuthList)) {
            //缓存用户权限信息
            roleAuthMap = roleAuthList.stream().collect(Collectors.toMap(RoleAuthVO::getCode, vo->vo.getDataCoverage().toString()));
            stringRedisTemplate.opsForHash().putAll("sys:auth:user:"+userId, roleAuthMap);
            stringRedisTemplate.expire("sys:auth:user:"+userId, 10, TimeUnit.MINUTES);
        }else {
            //缓存空值，防止缓存穿透问题
            stringRedisTemplate.opsForHash().putAll("sys:auth:user:"+userId, Map.of("",""));
            stringRedisTemplate.expire("sys:auth:user:"+userId, 10, TimeUnit.SECONDS);
        }
        return roleAuthList;
    }

    @Override
    public RoleAuthVO getPermission(Integer userId, String authCode){
        List<RoleAuthVO> roleAuthList = this.getPermissionList(userId);
        if (roleAuthList == null || roleAuthList.isEmpty()) {return null;}
        for (RoleAuthVO roleAuthVO : roleAuthList) {
            if (roleAuthVO.getCode().equals(authCode)) {
                return roleAuthVO;
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleDTO roleDTO) {
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);//拷贝属性
        roleMapper.addRole(role);// 添加角色
        Integer roleId = role.getId();//返回角色id
        if (CollectionUtils.isEmpty(roleDTO.getPermissions())) {return;}//判断权限列表是否为空
        // 组装权限关联数据（极简流式写法）
        List<RoleAuth> permissions = roleDTO.getPermissions().stream()
                .map(permission -> new RoleAuth(
                        roleId,
                        permission.getId(),
                        permission.getDataCoverage(),
                        null,
                        null
                )).toList();
        roleMapper.addRolePermission(permissions);// 添加角色与权限的对应关系
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleDTO roleDTO) {
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);//拷贝属性
        roleMapper.updateRole(role);// 修改角色
        roleMapper.deleteRolePermission(roleDTO.getId());// 删除角色权限关系
        List<RoleAuth> permissions = roleDTO.getPermissions().stream()
                .map(permission -> new RoleAuth(
                        roleDTO.getId(),
                        permission.getId(),
                        permission.getDataCoverage(),
                        null,
                        null
                )).toList();
        roleMapper.addRolePermission(permissions);// 添加角色权限关系
        //获取绑定该被修改角色的用户id列表
        List<Integer> userIds = roleMapper.selectUserIdsByRoleId(role.getId());
        //删除用户权限缓存
        //TODO 升级为最终一致性的延时双删或MQ异步删除缓存，或强一致性的Redisson分布式锁
        for (Integer userId : userIds) {
            stringRedisTemplate.delete("sys:auth:user:"+userId);
        }
    }

    @Override
    public PageResult<RoleSimpleVO> getAllRole(Integer page, Integer pageSize, String roleName) {
        Integer total = roleMapper.countRole(roleName);
        Integer skip = (page-1)*pageSize;
        if (roleName != null) {roleName = roleName.trim();}//去除空格
        List<RoleSimpleVO> items = roleMapper.getAllRole(skip, pageSize, roleName);
        return new PageResult<>(total, items);
    }

    @Override
    public RoleVO getRoleById(Integer id) {
        RoleVO roleVO = new RoleVO();
        Role role = roleMapper.getRoleById(id);// 获取角色信息
        if (role == null) {return null;}
        BeanUtils.copyProperties(role, roleVO);//拷贝属性
        List<RoleAuthVO> roleAuthList = roleMapper.getRoleAuth(id);
        roleVO.setPermissions(roleAuthList);
        return roleVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRole(List<Integer> ids) {
        roleMapper.batchDeleteUserRole(null,ids);//批量删除用户角色关系表中的对应记录
        roleMapper.batchDeleteRoleAuth(ids,null);//批量删除角色权限表中的对应记录
        roleMapper.batchDeleteRole(ids);//删除角色
    }

    @Override
    public List<RoleDTO> selectRoleNameByUserId(Integer id) {
        return roleMapper.selectRoleNameByUserId(id);
    }

    @Override
    public void addPermission(AuthDTO authDTO) {
        try{
            roleMapper.insertPermission(authDTO);
        }catch (Exception e){
            throw new BusinessException("权限已存在");
        }

    }

    @Override
    public void updatePermission(AuthDTO authDTO) {
        roleMapper.updatePermission(authDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeletePermission(List<Integer> ids) {
        roleMapper.batchDeleteRoleAuth(null,ids);//批量删除角色权限表中的对应记录
        roleMapper.batchDeletePermission(ids);
    }
}
