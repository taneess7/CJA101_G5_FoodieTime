package com.foodietime.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 啟用 WebSocket 訊息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 配置訊息代理 (Message Broker)。
     * 作用：定義訊息的路由規則。
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 啟用一個簡單的記憶體內訊息代理。
        // /topic 是廣播式，/queue 是點對點式 (一對一)
        config.enableSimpleBroker("/topic");

        // 設定客戶端發送訊息的目的地前綴。
        // 當客戶端發送訊息到 /app/xxx 時，會被路由到 @MessageMapping("/xxx") 的方法處理。
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 註冊 STOMP 端點。
     * 作用：定義客戶端用於連接 WebSocket 伺服器的 URL。
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 註冊一個名為 "/ws" 的端點。
        // setAllowedOriginPatterns("*") 允許所有來源的跨域請求。
        // withSockJS() 啟用 SockJS 回退選項，以支援不原生支援 WebSocket 的瀏覽器。
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}