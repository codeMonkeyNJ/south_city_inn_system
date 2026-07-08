package com.mason.config;


import com.mason.interceptor.WebSocketShakeInterceptor;
import com.mason.webSocket.BaseWebSocketHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class WebSocketConfig implements WebSocketConfigurer {
// 自定义消息处理器
@Resource
    private BaseWebSocketHandler baseWebSocketHandler;
    @Resource
    private WebSocketShakeInterceptor webSocketShakeInterceptor;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(baseWebSocketHandler, "/ws/**")
                .addInterceptors(webSocketShakeInterceptor)// 握手拦截器，连接建立前执行鉴权
                .setAllowedOrigins("*");//允许所有域名访问
    }
}
