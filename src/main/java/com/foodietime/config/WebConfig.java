package com.foodietime.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.foodietime.smg.loginInterceptor.SmgLoginInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SmgLoginInterceptor())
                .addPathPatterns("/smg/**")     // 需要登入的路徑
                .excludePathPatterns("/smg/login"); // 不攔截登入頁面
    }
}	