package com.mason.config;

import com.mason.interceptor.ClientTokenInterceptor;
import com.mason.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final TokenInterceptor tokenInterceptor;
    private final ClientTokenInterceptor clientTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自定义拦截器对象
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/sys/**")//拦截/sys开头所有请求
                .excludePathPatterns("/sys/user/login")//登录接口不拦截
                .order(1);//拦截器执行顺序
        registry.addInterceptor(clientTokenInterceptor)
                .addPathPatterns("/client/**")//拦截client开头的所有请求
                .excludePathPatterns("/client/login/code","/client/login","/client/register")//登录接口不拦截
                .order(1);//拦截器执行顺序
    }
}
