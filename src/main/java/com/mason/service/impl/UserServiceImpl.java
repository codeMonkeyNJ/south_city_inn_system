package com.mason.service.impl;

import com.mason.domain.PageResult;
import com.mason.domain.dto.DeptDTO;
import com.mason.domain.dto.RoleDTO;
import com.mason.domain.dto.UserDTO;
import com.mason.domain.dto.UserUpdatePasswordDTO;
import com.mason.domain.po.User;
import com.mason.domain.po.UserInfo;
import com.mason.domain.vo.UserSimpleVO;
import com.mason.domain.vo.UserVO;
import com.mason.exception.AuthorityException;
import com.mason.exception.BusinessException;
import com.mason.mapper.UserMapper;
import com.mason.service.DeptService;
import com.mason.service.RoleService;
import com.mason.service.UserService;
import com.mason.utils.SHA256;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DeptService deptService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public PageResult<UserSimpleVO> getAllUser(Integer page,
                                               Integer pageSize,
                                               String username,
                                               String nickname,
                                               String roleName,
                                               String deptName,
                                               Boolean state,
                                               LocalDate startTime,
                                               LocalDate endTime,
                                               Integer dataCoverage,
                                               Integer loginUserId) {
        List<Integer> validDepts = null;//有效部门id列表
        Integer skip = (page-1)*pageSize;
        Integer total = 0;//总记录数
        List<UserSimpleVO> items = null;//用户信息列表
        //判断数据范围
        switch (dataCoverage){
            case 0: //数据范围是所有
                total = userMapper.countUser(validDepts, username, nickname, roleName, deptName, state, startTime, endTime);
                items = userMapper.getAllUser(validDepts, skip, pageSize, null, username, nickname, roleName, deptName, state, startTime, endTime);
                break;
            case 1://数据范围是部门
                validDepts = deptService.getAllDeptIdsByUserId(loginUserId);//获取登录用户所属部门以及下属部门id
                total = userMapper.countUser(validDepts, username, nickname, roleName, deptName, state, startTime, endTime);
                items = userMapper.getAllUser(validDepts, skip, pageSize, null, username, nickname, roleName, deptName, state, startTime, endTime);
                break;
            case 2://数据范围是个人
                total = 1;
                items = userMapper.getAllUser(validDepts, skip, pageSize, loginUserId, username, nickname, roleName, deptName, state, startTime, endTime);
                break;
        }

        if (total == 0){return new PageResult<>(0, null);}
        return new PageResult<>(total, items);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserDTO userDTO, Integer loginUserId, Integer dataCoverage){
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);//属性拷贝
        //加密初始密码
        String phone = userDTO.getPhone();
        String last6 = phone.substring(phone.length()-6);//获取手机号的后6位
        String passwordSHA256 = SHA256.getSHA256(last6);//进行SHA256加密
        user.setPassword(encoder.encode(passwordSHA256));//进行BCrypt加密
        userMapper.addUser(user);//添加用户的账号信息
        Integer userId = user.getId();
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userDTO,userInfo);//属性拷贝
        userInfo.setUserId(userId);
        userMapper.addUserInfo(userInfo);//添加用户的详细信息
        if(!CollectionUtils.isEmpty(userDTO.getRoleIds())){
            userMapper.addUserRole(userId,userDTO.getRoleIds());//为用户绑定角色
        }
        if (!CollectionUtils.isEmpty(userDTO.getDeptIds())){
            userMapper.addUserDept(userId,userDTO.getDeptIds());//为用户绑定部门
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO userDTO, Integer loginUserId, Integer dataCoverage) {
        Integer updateUserId = userDTO.getId();
        //数据权限检查
        switch (dataCoverage){
            case 2:
                if (!Objects.equals(loginUserId, updateUserId)){throw new AuthorityException("权限不足");}
                break;
            case 1:
                List<Integer> loginUserDepts = deptService.getAllDeptIdsByUserId(loginUserId);//获取登录用户所属部门以及下属部门ids
                List<Integer> updateUserDepts = deptService.getDeptIdsByUserId(updateUserId);//获取用户所属部门ids
                //判断要修改的用户是否属于当前登录用户所在的部门或子部门下
                if(!new HashSet<>(loginUserDepts).containsAll(updateUserDepts)){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);//属性拷贝
        userMapper.updateUser(user);//更新用户的账号信息
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userDTO,userInfo);//属性拷贝
        userInfo.setUserId(updateUserId);
        userMapper.updateUserInfo(userInfo);//更新用户的详细信息
        if (dataCoverage!=0){return;}//只有数据范围是全部才允许修改角色和部门信息
        userMapper.deleteUserRole(updateUserId);//删除用户原有的角色
        if (!CollectionUtils.isEmpty(userDTO.getRoleIds())){
            userMapper.addUserRole(updateUserId,userDTO.getRoleIds());//为用户绑定角色
        }
        //TODO 升级为最终一致性的延时双删或MQ异步删除缓存，或强一致性的Redisson分布式锁
        stringRedisTemplate.delete("sys:user:"+loginUserId);//删除缓存中的权限信息
        userMapper.deleteUserDept(updateUserId);//删除用户原有的部门
        if (!CollectionUtils.isEmpty(userDTO.getDeptIds())){
            userMapper.addUserDept(updateUserId,userDTO.getDeptIds());//为用户绑定部门
        }
    }

    @Override
    public UserVO getUserById(Integer id, Integer loginUserId, Integer dataCoverage) {
        //数据权限检查
        switch (dataCoverage){
            case 2:
                if (!Objects.equals(loginUserId, id)){throw new AuthorityException("权限不足");}
                break;
            case 1:
                List<Integer> loginUserDepts = deptService.getAllDeptIdsByUserId(loginUserId);//获取登录用户所属部门以及下属部门ids
                List<Integer> targetUserDepts = deptService.getDeptIdsByUserId(id);//获取用户所属部门ids
                //判断查询的用户是否属于当前登录用户所在的部门或子部门下
                if(!new HashSet<>(loginUserDepts).containsAll(targetUserDepts)){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        User user = userMapper.selectUserById(id);//查询用户账号信息
        UserInfo userInfo = userMapper.selectUserInfoByid(id);//查询用户详细信息
        List<RoleDTO> roleList = roleService.selectRoleNameByUserId(id);//查询用户绑定的角色
        List<DeptDTO> deptList = deptService.selectDeptNameByUserId(id);//查询用户绑定的部门
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);//属性拷贝
        BeanUtils.copyProperties(userInfo,userVO);//属性拷贝
        String role = roleList.stream().map(RoleDTO::getName).collect(Collectors.joining(","));//拼接角色
        String dept = deptList.stream().map(DeptDTO::getName).collect(Collectors.joining(","));//拼接部门
        userVO.setRole(role);
        userVO.setDept(dept);
        userVO.setDeptIds(deptList.stream().map(DeptDTO::getId).collect(Collectors.toList()));
        userVO.setRoleIds(roleList.stream().map(RoleDTO::getId).collect(Collectors.toList()));
        return userVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO, Integer loginUserId, Integer dataCoverage) {
        Integer userId = userUpdatePasswordDTO.getId();//用户id
        //数据权限检查
        switch (dataCoverage){
            case 2:
                if (!Objects.equals(loginUserId, userId)){throw new AuthorityException("权限不足");}
                break;
            case 1:
                List<Integer> loginUserDepts = deptService.getAllDeptIdsByUserId(loginUserId);//获取登录用户所属部门以及下属部门ids
                List<Integer> targetUserDepts = deptService.getDeptIdsByUserId(userId);//获取用户所属部门ids
                //判断查询的用户是否属于当前登录用户所在的部门或子部门下
                if(!new HashSet<>(loginUserDepts).containsAll(targetUserDepts)){throw new AuthorityException("权限不足");}
                break;
            case 0:
                break;
        }
        String outPassword = userUpdatePasswordDTO.getOldPassword();//用户输入的原密码
        String newPassword= userUpdatePasswordDTO.getNewPassword();//用户输入的新密码
        String nowPassword = userMapper.selectUserPassword(userId);//获取当前存储的密码
        if(!encoder.matches(outPassword,nowPassword)){
            throw new BusinessException("密码错误");
        }
        String password = encoder.encode(newPassword);//密码加密
        userMapper.updateUserPassword(userId,password);//修改密码
    }

    @Override
    public String selectNameByUserId(Integer userId) {
        UserInfo userInfo = userMapper.selectUserInfoByid(userId);
        return userInfo.getName();
    }


}
