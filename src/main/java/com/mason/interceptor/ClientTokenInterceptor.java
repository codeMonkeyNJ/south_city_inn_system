package com.mason.interceptor;


import com.mason.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ClientTokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler){
        log.info("ClientTokenInterceptor 拦截到了请求");
        String jwt = request.getHeader("token");//获取请求头中的令牌（token）。
        //判断令牌是否存在，如果不存在，返回错误结果（未登录）。
        if(!StringUtils.hasLength(jwt)){ //jwt为空
            log.info("当前用户未登录");
            request.setAttribute("loginUserId",-1);//将用户id保存到request中,-1表示未登录
            return true;
        }
        Claims claims;
        //解析token，如果解析失败，返回错误结果（未登录）。
        try {
            claims = JwtUtils.parseJWT(jwt);
        } catch (Exception e) {
            log.error("解析令牌失败",e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        Integer userId = (Integer) claims.get("id");
        request.setAttribute("loginUserId",userId);//将用户id保存到request中
        return true;
    }
}
