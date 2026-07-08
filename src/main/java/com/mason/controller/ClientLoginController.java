package com.mason.controller;

import com.mason.domain.Result;
import com.mason.domain.dto.ClientLoginDTO;
import com.mason.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/client")
public class ClientLoginController {
    @Autowired
    private LoginService loginService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody ClientLoginDTO clientLoginDTO) throws Exception{
        try {
            loginService.clientRegister(clientLoginDTO);
        } catch (DuplicateKeyException e){
            return Result.error("400","用户已存在");
        } catch (ExecutionException e){
            log.error("验证码错误",e);
            return Result.error("400","验证码错误");
        }
        return Result.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody ClientLoginDTO clientLoginDTO) throws Exception {
        try{
            String jwt = loginService.clientLogin(clientLoginDTO);
            return Result.success(Map.of("token", jwt));
        } catch (ExecutionException e){
            log.error("验证码错误",e);
            return Result.error("400","验证码错误");
        }
    }

    /**
     * 获取验证码
     */
    @PostMapping("/login/code")
    public Result getCode(String phone) throws Exception {
        try {
            loginService.getCode(phone);
        } catch (ExecutionException e){
            log.error("短信发送失败",e);
            return Result.error("500","短信发送失败");
        } catch (InterruptedException e){
            log.error("短信发送中断",e);
            return Result.error("500","短信发送中段");
        }
        return Result.success();
    }
}
