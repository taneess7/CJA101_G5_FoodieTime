package com.foodietime.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP 配置類別
 * 啟用 AspectJ 自動代理功能
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
    // 這個配置類別會自動啟用 AOP 功能
    // Spring Boot 通常會自動配置，但明確宣告可以確保 AOP 功能正常運作
}