package com.mason.service;

import com.mason.domain.PageResult;
import com.mason.domain.dto.UserDTO;
import com.mason.domain.dto.UserUpdatePasswordDTO;
import com.mason.domain.vo.UserSimpleVO;
import com.mason.domain.vo.UserVO;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public interface UserService {
    /**
     * 获取所有用户信息
     */
    PageResult<UserSimpleVO> getAllUser(Integer page,
                                               Integer pageSize,
                                               String username,
                                               String nickname,
                                               String roleName,
                                               String deptName,
                                               Boolean state,
                                               LocalDate startTime,
                                               LocalDate endTime,
                                               Integer dataCoverage,
                                               Integer loginUserId);
    /**
     * 添加用户
     */
    void addUser(UserDTO userDTO, Integer loginUserId, Integer dataCoverage) throws NoSuchAlgorithmException;

    /**
     * 修改用户
     */
    void updateUser(UserDTO userDTO, Integer loginUserId, Integer dataCoverage);

    /**
     * 根据id查询用户
     */
    UserVO getUserById(Integer id, Integer loginUserId, Integer dataCoverage);

    /**
     * 修改用户密码
     */
    void updatePassword(UserUpdatePasswordDTO userUpdatePasswordDTO, Integer loginUserId, Integer dataCoverage);

    /**
     * 根据用户id查询用户的真实姓名
     */
    String selectNameByUserId(Integer userId);
}
