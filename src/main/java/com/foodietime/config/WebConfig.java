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
        
        // 會員頁 禁止快取
        registry.addInterceptor(new NoCacheInterceptor())
                .addPathPatterns("/front/member/**");

        // 會員頁 強制登入
        registry.addInterceptor(new MemberLoginInterceptor())
                .addPathPatterns(
                    "/front/member/member_center",
                    "/front/member/edit_profile",
                    "/front/member/messages",
                    "/front/member/coupons",
                    "/front/member/orders"
                )
                .excludePathPatterns(
                    "/front/member/login",
                    "/front/member/register",
                    "/front/member/success",
                    "/front/member/storeregister"
                );
    }
}	