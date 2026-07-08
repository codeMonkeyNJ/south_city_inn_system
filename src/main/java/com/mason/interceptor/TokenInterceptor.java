package com.mason.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mason.anno.AuthCode;
import com.mason.domain.vo.RoleAuthVO;
import com.mason.service.RoleService;
import com.mason.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    //目标资源方法执行前执行。 返回true：放行    返回false：不放行
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        log.info("TokenInterceptor 拦截到了请求");
        String jwt = request.getHeader("token");//获取请求头中的令牌（token）。
        //判断令牌是否存在，如果不存在，返回错误结果（未登录）。
        if(!StringUtils.hasLength(jwt)){ //jwt为空
            log.info("获取到jwt令牌为空, 未登录");
            setErrorResult(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录");
            return false;
        }
        Claims claims;
        //解析token，如果解析失败，说明令牌被修改。
        try {
            claims = JwtUtils.parseJWT(jwt);
        } catch (Exception e) {
            log.error("解析令牌失败",e);
            setErrorResult(response, HttpServletResponse.SC_UNAUTHORIZED, "解析令牌失败");
            return false;
        }
        Integer userId = (Integer) claims.get("id");
        String token = stringRedisTemplate.opsForValue().get("sys:token:user:" + userId);
        if (token==null){//userId不在redis中（用户已登出）
            log.error("用户未登录");
            setErrorResult(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录");
            return false;
        } else if (!token.equals(jwt)) {//携带的令牌不是redis中存储的令牌
            log.error("令牌失败");
            setErrorResult(response, HttpServletResponse.SC_UNAUTHORIZED, "令牌失效");
            return false;
        }
        stringRedisTemplate.expire("sys:token:user:" + userId,1, TimeUnit.HOURS);//令牌续期
        request.setAttribute("loginUserId",userId);//将用户id保存到request中
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AuthCode authCode = handlerMethod.getMethodAnnotation(AuthCode.class);//获取方法上的权限注解
        if (authCode==null || authCode.value().isEmpty()){
            log.info("接口不需要权限校验, 放行");
            return true;
        }
        Map<Object, Object> authMap = stringRedisTemplate.opsForHash().entries("sys:auth:user:" + userId);
        if (!CollectionUtils.isEmpty(authMap)){//缓存中存在用户权限列表
            String dataCoverage = (String)authMap.get(authCode.value());// 获取用户对该接口的数据范围
            if (dataCoverage != null){//用户权限列表中存在该权限
                log.info("权限合法, 放行");
                request.setAttribute("dataCoverage",Integer.valueOf(dataCoverage));//将数据服务范围保存到request中
                return true;
            }else{//用户权限列表中不存在该权限
                log.info("权限不足，拦截");
                setErrorResult(response, HttpServletResponse.SC_FORBIDDEN, "权限不足");
                return false; //拦截
            }
        }else{//缓存中不存在用户权限列表
            RoleAuthVO roleAuthVO = roleService.getPermission(userId, authCode.value());//获取用户权限信息
            if(roleAuthVO == null){
                log.info("权限不足，拦截");
                setErrorResult(response, HttpServletResponse.SC_FORBIDDEN, "权限不足");
                return false; //拦截
            }
            log.info("权限合法, 放行");
            request.setAttribute("dataCoverage",roleAuthVO.getDataCoverage());//将数据服务范围保存到request中
            return true;
        }
    }
    private void setErrorResult(HttpServletResponse response,Integer code, String msg) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=utf-8");
        Map<String, Object> res = new HashMap<>();
        res.put("code", code.toString());
        res.put("msg", msg);
        res.put("data", null);
        response.getWriter().print(objectMapper.writeValueAsString(res));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
