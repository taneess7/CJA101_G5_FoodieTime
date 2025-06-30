package com.foodietime.orders.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service // 將其標記為 Service，使其可被注入
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 透過建構子注入 SimpMessagingTemplate
     * @param messagingTemplate Spring 自動配置的 STOMP 訊息發送模板
     */
    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 以異步方式發送新訂單通知給指定的店家。
     * 使用 SimpMessagingTemplate 將訊息發送到特定的 STOMP 主題 (Topic)。
     *
     * @param storeId 目標商家的會員 ID (memId)，用於構成唯一的 Topic 路徑
     * @param message 要發送的 JSON 格式的訂單通知訊息
     */
    @Async
    public void sendOrderNotificationAsync(Integer storeId, String message) {
        // ==================== 核心修改 ====================
        // 定義訊息要發送的目的地 (Topic)
        // 格式為 /topic/orders/{storeId}，這樣每個店家只會收到自己的訂單通知
        String destination = "/topic/orders/" + storeId;

        // 使用 messagingTemplate 發送訊息
        // convertAndSend 會自動將 message 字串包裝成 STOMP 訊息並發送到指定目的地
        messagingTemplate.convertAndSend(destination, message);
    }
}