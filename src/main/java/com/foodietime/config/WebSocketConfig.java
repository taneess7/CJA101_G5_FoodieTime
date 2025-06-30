
package com.foodietime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 組態類。
 * 這個 Bean 會自動註冊所有使用 @ServerEndpoint 註解宣告的 WebSocket endpoint。
 */
@Configuration
@EnableAsync
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

