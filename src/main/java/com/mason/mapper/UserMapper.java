package com.mason.mapper;

import com.mason.domain.po.User;
import com.mason.domain.po.UserInfo;
import com.mason.domain.vo.UserSimpleVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 统计总记录数
     */
    Integer countUser(List<Integer> validDepts, String username, String nickname, String roleName, String deptName, Boolean state, LocalDate startTime, LocalDate endTime);

    /**
     * 获取所有用户信息
     */
    List<UserSimpleVO> getAllUser(List<Integer> validDepts, Integer skip, Integer pageSize, Integer userId, String username, String nickname, String roleName, String deptName, Boolean state, LocalDate startTime, LocalDate endTime);

    /**
     * 添加用户账号信息
     */
    void addUser(User user);
    /**
     * 添加用户详细信息
     */
    void addUserInfo(UserInfo userInfo);

    /**
     * 添加用户角色关系
     */
    void addUserRole(Integer userId, List<Integer> roleIds);

    /**
     * 添加用户部门关系
     */
    void addUserDept(Integer userId, List<Integer> deptIds);

    /**
     * 修改用户账号信息
     */
    void updateUser(User user);

    /**
     * 修改用户详细信息
     */
    void updateUserInfo(UserInfo userInfo);

    /**
     * 删除用户角色关系
     */
    @Delete("delete from user_role where user_id=#{userId}")
    void deleteUserRole(Integer userId);

    /**
     * 删除用户部门关系
     */
    @Delete("delete from user_dept where user_id= #{userId}")
    void deleteUserDept(Integer userId);

    /**
     * 根据id查询用户
     * @param id 用户id
     * @return 用户账号信息（用户名、昵称、头像、状态）
     */
    @Select("select id, username, nickname, avatar, state from user where id=#{id}")
    User selectUserById(Integer id);

    /**
     * 根据id查询用户详细信息
     * @param id 用户id
     * @return 用户详细信息
     */
    @Select("select name, gender, birthday, id_card, address, bank_name, bank_card, salary, phone, remark from user_info where user_id= #{id}")
    UserInfo selectUserInfoByid(Integer id);


    /**
     * 查询用户密码
     * @param id 用户id
     * @return 用户密码密文
     */
    @Select("select password from user where id= #{id}")
    String selectUserPassword(Integer id);

    /**
     * 修改用户密码
     * @param userId 用户id
     * @param password 密码密文
     */
    @Update("update user set password= #{password} where id= #{userId}")
    void updateUserPassword(Integer userId, String password);

    /**
     * 根据用户名查询用户id
     * @param username 用户名
     * @return 用户id
     */
    @Select("select id from user where username= #{username}")
    Integer selectUserIdByUsername(String username);

}
