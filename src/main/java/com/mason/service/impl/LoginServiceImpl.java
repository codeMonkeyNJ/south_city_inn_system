package com.mason.service.impl;


import com.mason.domain.dto.ClientLoginDTO;
import com.mason.domain.dto.LoginDTO;
import com.mason.domain.po.Client;
import com.mason.domain.po.User;
import com.mason.exception.BusinessException;
import com.mason.mapper.LoginMapper;
import com.mason.mapper.UserMapper;
import com.mason.service.LoginService;
import com.mason.utils.JwtUtils;
import com.mason.utils.RandomUsernameUtils;
import com.mason.utils.SmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SmsUtils smsUtils;
    @Override
    public String login(LoginDTO loginDTO) {
        String savePassword = loginMapper.getPassword(loginDTO.getUsername());//获取保存的密码
        if (!encoder.matches(loginDTO.getPassword(), savePassword)) {
            throw new BusinessException("用户名或密码错误");
        }
        Integer userId = userMapper.selectUserIdByUsername(loginDTO.getUsername());//获取用户id
        User user = userMapper.selectUserById(userId);
        if (!user.getState()){
            throw new BusinessException("用户已被禁用");
        }
        HashMap<String, Object> dateMap = new HashMap<>();
        dateMap.put("id",userId);
//        dateMap.put("loginTime",System.currentTimeMillis());//登录时间(用于改变令牌，以免令牌被重复使用)
        String jwt = JwtUtils.generateJwt(dateMap);
        //缓存jwt
        stringRedisTemplate.opsForValue().set("sys:token:user:" + userId,jwt,1, TimeUnit.HOURS);
        return jwt;
    }

    @Override
    public void logout(Integer loginUserId) {
        stringRedisTemplate.delete("sys:token:user:" + loginUserId);//删除缓存的jwt
        stringRedisTemplate.delete("sys:auth:user:" + loginUserId);//删除缓存的权限
    }

    @Override
    public String clientLogin(ClientLoginDTO clientLoginDTO) throws Exception {
        Client client = loginMapper.selectClientLoginInfo(clientLoginDTO.getUsername());
        if (client == null){
            throw new BusinessException("用户不存在");
        }
        if(clientLoginDTO.getMode() == 0){//账号密码登录
            if (!encoder.matches(clientLoginDTO.getPassword(), client.getPassword())) {
                throw new BusinessException("用户名或密码错误");
            }
        } else if (clientLoginDTO.getMode() == 1) {//手机号登录
            smsUtils.checkSms(clientLoginDTO.getPhone(), clientLoginDTO.getCode());
        }
        HashMap<String, Object> dateMap = new HashMap<>();
        dateMap.put("id",client.getId());
        return JwtUtils.generateJwt(dateMap);
    }

    @Override
    public void clientRegister(ClientLoginDTO clientLoginDTO) throws Exception {
        if (clientLoginDTO.getUsername()==null){
            clientLoginDTO.setUsername(RandomUsernameUtils.generateUsername());//生成随机用户名
        }
        if (clientLoginDTO.getMode() == 0){//账号密码注册
            if(!StringUtils.hasText(clientLoginDTO.getPassword())){
                throw new BusinessException("请输入密码");
            }
            clientLoginDTO.setPassword(encoder.encode(clientLoginDTO.getPassword()));//进行BCrypt加密
        } else if (clientLoginDTO.getMode() == 1) {//手机号注册
            if(!StringUtils.hasText(clientLoginDTO.getPhone())){
                throw new BusinessException("请输入手机号");
            }
            smsUtils.checkSms(clientLoginDTO.getPhone(), clientLoginDTO.getCode());
        }
        loginMapper.insertClient(clientLoginDTO);
    }

    @Override
    public void getCode(String phone) throws Exception {
        smsUtils.sendSms(phone);//发送验证码
    }

}
