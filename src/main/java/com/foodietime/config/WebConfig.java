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
                    "/member/coupons/**",
                    "/member/orders/**",
                    "/cart/**",
                    "/favorite/**",
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
        
        // 🔺記錄登入前頁面，供登入成功後導回
        registry.addInterceptor(new RecordRefererInterceptor())
                .addPathPatterns("/category/**");  // 只記錄這些公開分類頁


    }   
}	