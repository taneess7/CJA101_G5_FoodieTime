package com.foodietime.orders.model;

import com.foodietime.orders.websocket.OrderNotificationWebSocket;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationService {

    /**
     * 以異步方式發送新訂單通知給店家。
     * @Async 會讓這個方法在另一個執行緒中執行，不會阻塞主流程。
     *
     * @param storeId 目標商家的 ID
     * @param message 要發送的 JSON 訊息
     */
    @Async
    public void sendOrderNotificationAsync(Integer storeId, String message) {
        // 這裡的邏輯和之前完全一樣
        OrderNotificationWebSocket.sendNotificationToStore(storeId, message);
    }
}