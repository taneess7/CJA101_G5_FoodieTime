package com.foodietime.orders.websocket;

import jakarta.websocket.*;

import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/orders/{storeId}")
public class OrderNotificationWebSocket {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationWebSocket.class);

    /**
     * 線上商家的 WebSocket Session 儲存。
     * Key: storeId (Integer)
     * Value: Session
     */
    private static final Map<Integer, Session> onlineStores = new ConcurrentHashMap<>();

    /**
     * 當 WebSocket 連線建立時呼叫。
     * @param session WebSocket Session
     * @param storeId 從 URL 路徑中獲取的商家 ID
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("storeId") Integer storeId) {
        onlineStores.put(storeId, session);
        log.info("商家 [{}] 已連線，目前線上商家數：{}", storeId, onlineStores.size());
    }

    /**
     * 當 WebSocket 連線關閉時呼叫。
     * @param session WebSocket Session
     * @param storeId 商家 ID
     */
    @OnClose
    public void onClose(Session session, @PathParam("storeId") Integer storeId) {
        onlineStores.remove(storeId);
        log.info("商家 [{}] 已離線，目前線上商家數：{}", storeId, onlineStores.size());
    }

    /**
     * 當 WebSocket 發生錯誤時呼叫。
     * @param session WebSocket Session
     * @param error 錯誤資訊
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 發生錯誤", error);
    }

    /**
     * 伺服器端主動推送訊息給指定商家。
     * 此方法將由其他 Service 呼叫。
     * @param storeId 目標商家的 ID
     * @param message 要發送的訊息 (建議為 JSON 格式的訂單資料)
     */
    public static void sendNotificationToStore(Integer storeId, String message) {
        Session session = onlineStores.get(storeId);
        if (session != null && session.isOpen()) {
            try {
                // 使用 getAsyncRemote() 進行非同步發送
                session.getAsyncRemote().sendText(message);
                log.info("已成功推送新訂單通知給商家 [{}]", storeId);
            } catch (Exception e) {
                log.error("推送通知給商家 [{}] 失敗", storeId, e);
            }
        } else {
            log.warn("嘗試推送通知，但商家 [{}] 不在線上或 Session 已關閉", storeId);
        }
    }
}
