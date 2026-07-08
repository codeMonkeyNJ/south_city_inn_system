package com.mason.service;


import com.mason.domain.dto.ClientLoginDTO;
import com.mason.domain.dto.LoginDTO;


public interface LoginService {
    /**
     * 登录
     */
     String login(LoginDTO loginDTO);

    /**
     * 退出登录
     * @param loginUserId 登录用户ID
     */
    void logout(Integer loginUserId);

    /**
     * 客户端登录
     */
    String clientLogin(ClientLoginDTO clientLoginDTO) throws Exception;

    /**
     * 客户端注册
     */
    void clientRegister(ClientLoginDTO clientLoginDTO) throws Exception;

     /**
     * 获取验证码
     * @param phone 手机号
     */
    void getCode(String phone) throws Exception;

}
