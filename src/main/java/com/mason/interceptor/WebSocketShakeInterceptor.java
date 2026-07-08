package com.mason.interceptor;

import com.mason.domain.dto.RoleAuthSimDTO;
import com.mason.domain.vo.RoleAuthVO;
import com.mason.service.RoleService;
import com.mason.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class WebSocketShakeInterceptor implements HandshakeInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RoleService roleService;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) throws Exception {
        log.info("进入websocket握手拦截器");
        URI uri = request.getURI();
        String path = uri.getPath();// 获取请求路径：/ws/xxxx-token-xxxx
        String[] pathArr = path.split("/");// 按 / 分割路径
        String token = null;
        if (pathArr.length >= 3) {// 路径格式固定 /ws/{token}，数组长度一定是3
            token = pathArr[2];
        }
        Claims claims;
        try{
            claims = JwtUtils.parseJWT(token);//解析JWT令牌
        }catch (Exception e){
            log.error("解析JWT令牌失败",e);
            return false;
        }
        Integer userId = (Integer) claims.get("id");//获取用户id
        if (!stringRedisTemplate.hasKey("sys:token:user:" + userId)){return false;}//用户未登录

        // 获取用户监听权限
        List<RoleAuthSimDTO> listenAuthList = new ArrayList<>();
        Map<Object, Object> authMap = stringRedisTemplate.opsForHash().entries("sys:auth:user:" + userId);
        if (!CollectionUtils.isEmpty(authMap)) {//缓存中存在用户权限列表
            String purchaseApplyDC = (String)authMap.get("purchase-apply-listen");
            if (purchaseApplyDC != null) {
                listenAuthList.add(new RoleAuthSimDTO("purchase-apply-listen", Integer.parseInt(purchaseApplyDC)));
            }
        }else{
            RoleAuthVO roleAuthVO = roleService.getPermission(userId, "purchase-apply-listen");//获取用户权限信息
            if (roleAuthVO != null) {
                listenAuthList.add(new RoleAuthSimDTO("purchase-apply-listen", roleAuthVO.getDataCoverage()));
            }
        }

        if (listenAuthList.isEmpty()) { return false; }//用户不需要建立websocket连接
        attributes.put("userId",userId);
        attributes.put("listenAuthList",listenAuthList);
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
