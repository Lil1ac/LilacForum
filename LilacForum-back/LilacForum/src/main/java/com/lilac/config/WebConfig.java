package com.lilac.config;

import com.lilac.interceptor.LoginCheckInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")  // 前端的地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置拦截器
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")          // 拦截所有请求
                .excludePathPatterns("/login")    // 排除登录请求
                .excludePathPatterns("/register") // 排除注册请求
                .excludePathPatterns("/public/**") // 排除公共资源请求（可根据需要调整）
                .excludePathPatterns("/error");   // 排除错误处理请求（如果有的话）
    }

}
