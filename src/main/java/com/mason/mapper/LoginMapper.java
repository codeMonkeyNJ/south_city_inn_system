package com.mason.mapper;

import com.mason.domain.dto.ClientLoginDTO;
import com.mason.domain.po.Client;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper {
    /**
     * 根据用户名查询密码
     * @param username 用户名
     * @return 密码
     */
    @Select("select password from user where username = #{username}")
    String getPassword(String username);

    /**
     * 插入用户
     * @param clientLoginDTO 用户信息
     */
    void insertClient(ClientLoginDTO clientLoginDTO);


    /**
     * 根据用户名查询用户登录信息
     * @param username 用户名
     * @return 用户信息
     */
    @Select("select id,username,password,phone from client where username = #{username}")
    Client selectClientLoginInfo(String username);
}
