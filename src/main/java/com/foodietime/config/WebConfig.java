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
                .excludePathPatterns(
                    "/smg/login",
                    "/smg/admin/login",
                    "/smg/logout",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ); // 不攔截登入頁面、登出、靜態資源
        
        // 會員頁 禁止快取
        registry.addInterceptor(new NoCacheInterceptor())
                .addPathPatterns("/front/member/**");

        // 會員頁 強制登入
        registry.addInterceptor(new MemberLoginInterceptor())
                .addPathPatterns(
                    "/front/member/member_center",
                    "/front/member/edit_profile",
                    "/front/member/messages",
                    "/member/coupons/**",
                    "/member/orders/**",
                    "/cart/**",
                    "/favorite/**",
                    "/category/**",
                    "/post/**"
                    
                )
                .excludePathPatterns(
                	"/front/member/login",
                    "/front/member/register",
                    "/front/member/verify",
                    "/front/member/activate",
                    "/front/member/logout",
                    "/front/member/storeregister"
                );
        // 店家 Interceptor
        registry.addInterceptor(new StoreLoginInterceptor())
                .addPathPatterns("/store/**")
                .excludePathPatterns(
                    "/store/desert2",         // 公開瀏覽的頁面不要攔
                    "/store/DBGifReader"      // 顯示圖片 endpoint 不應攔截
                );

    }   
}	