package com.foodietime.smg.controller;

// 給後台用的

import com.foodietime.orddet.dto.OrderDetailItemDTO;
import com.foodietime.orddet.model.OrdDetVO;
import com.foodietime.orders.dto.OrderDetailsDTO;
import com.foodietime.orders.dto.UpdateOrderStatusRequestDTO;
import com.foodietime.orders.model.OrdersService;
import com.foodietime.orders.model.OrdersVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 後台管理系統 (SMG) - 一般訂單管理控制器
 * 負責處理 /smg/orders/** 路徑下的所有請求
 */
@Controller
@RequestMapping("/smg")
public class AdminOrdersController {


    private final OrdersService ordersService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public AdminOrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    /**
     * 顯示後台「一般訂單詳情檢視」頁面。
     * 此方法會查詢所有訂單，並將其傳遞給前端 Thymeleaf 模板進行渲染。
     *
     * @param model 用於將數據傳遞到視圖的 Spring Model 物件。
     * @return 訂單管理頁面的視圖路徑。
     */
    @GetMapping("/admin-orders-view")
    public String showAdminOrdersPage(Model model) {
        // ==================== 1. 查找所有訂單基礎資料 ====================
        List<OrdersVO> ordersList = ordersService.findAllOrders();

        // ==================== 2. 將訂單列表加入 Model ====================
        model.addAttribute("ordersList", ordersList);

        // ==================== 3. 返回 Thymeleaf 模板的路徑 ====================
        // 請確保您的 admin-orders-view.html 位於 /resources/templates/smg/orders/view.html
        return "admin/smg/admin-orders";
    }

    /**
     * API 端點：根據訂單 ID 獲取單筆訂單的完整詳細資訊。
     * 此 API 被前端 JavaScript 的 `viewOrder` 函式呼叫。
     * 返回一個包含所有必要資訊的 DTO，避免直接暴露 Entity。
     *
     * @param orderId 從 URL 路徑中獲取的訂單 ID。
     * @return 如果找到訂單，返回包含 OrderDetailsDTO 的 ResponseEntity (HTTP 200)；
     *         如果找不到，返回 HTTP 404 Not Found。
     */
    @GetMapping("/{orderId}")
    @ResponseBody
    public ResponseEntity<OrderDetailsDTO> getOrderDetails(@PathVariable Integer orderId) {
        // ==================== 1. 呼叫 Service 獲取訂單及其明細 ====================
        Optional<OrdersVO> orderOpt = ordersService.findByIdWithDetails(orderId);

        // ==================== 2. 處理查詢結果 ====================
        return orderOpt.map(this::convertToDetailsDTO) // 若存在，則轉換為 DTO
                .map(ResponseEntity::ok) // 將 DTO 包裝成 HTTP 200 回應
                .orElseGet(() -> ResponseEntity.notFound().build()); // 若不存在，返回 HTTP 404
    }

    /**
     * API 端點：更新訂單狀態。
     * 此 API 被前端 JavaScript 的 `saveOrderChanges` 函式呼叫。
     *
     * @param requestDTO 包含訂單 ID 和要更新的狀態資訊的請求體。
     * @return 如果更新成功，返回成功的訊息 (HTTP 200)；
     *         如果發生錯誤（如找不到訂單），返回錯誤訊息 (HTTP 400)。
     */
    @PostMapping("/update-status")
    @ResponseBody
    public ResponseEntity<?> updateOrderStatus(@RequestBody UpdateOrderStatusRequestDTO requestDTO) {
        try {
            // ==================== 1. 呼叫 Service 執行更新邏輯 ====================
            ordersService.updateOrderStatus(
                    requestDTO.getOrdId(),
                    requestDTO.getOrderStatus(),
                    requestDTO.getPaymentStatus(),
                    requestDTO.getCancelReason()
            );

            // ==================== 2. 返回成功響應 ====================
            Map<String, String> response = new HashMap<>();
            response.put("message", "訂單 " + requestDTO.getOrdId() + " 狀態更新成功！");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // ==================== 3. 捕獲異常並返回錯誤響應 ====================
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 將 OrdersVO 實體物件轉換為前端所需的 OrderDetailsDTO。
     * 這是一個私有的輔助方法，用於實現 Entity 到 DTO 的轉換邏輯。
     *
     * @param order 原始的 OrdersVO 實體。
     * @return 轉換後的 OrderDetailsDTO 物件。
     */
    private OrderDetailsDTO convertToDetailsDTO(OrdersVO order) {
        OrderDetailsDTO dto = new OrderDetailsDTO();

        // --- 映射訂單主體資訊 ---
        dto.setOrdId(order.getOrdId());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        if (order.getOrdDate() != null) {
            dto.setOrdDate(order.getOrdDate().toLocalDateTime().format(FORMATTER));
        }
        dto.setPayMethod(order.getPayMethod());
        dto.setDeliver(order.getDeliver());
        dto.setOrdAddr(order.getOrdAddr());
        dto.setRating(order.getRating());
        dto.setComment(order.getComment());
        dto.setCancelReason(order.getCancelReason());

        // --- 映射金額資訊 ---
        dto.setOrdSum(order.getOrdSum());
        dto.setPromoDiscount(order.getPromoDiscount());
        dto.setCouponDiscount(order.getCouponDiscount());
        dto.setActualPayment(order.getActualPayment());

        // --- 映射關聯物件資訊 (會員、店家) ---
        if (order.getMember() != null) {
            dto.setMemberId(order.getMember().getMemId());
            dto.setMemberName(order.getMember().getMemName());
        }
        if (order.getStore() != null) {
            dto.setStoreId(order.getStore().getStorId());
            dto.setStoreName(order.getStore().getStorName());
        }

        // --- 映射訂單明細列表 (OrdDetVO -> OrderDetailItemDTO) ---
        List<OrderDetailItemDTO> detailItems = order.getOrdDet().stream()
                .map(this::convertDetToItemDTO)
                .collect(Collectors.toList());
        dto.setOrdDet(detailItems);

        return dto;
    }

    /**
     * 將 OrdDetVO 轉換為 OrderDetailItemDTO。
     */
    private OrderDetailItemDTO convertDetToItemDTO(OrdDetVO det) {
        OrderDetailItemDTO itemDTO = new OrderDetailItemDTO();
        if (det.getProduct() != null) {
            itemDTO.setProdName(det.getProduct().getProdName());
        }
        itemDTO.setOrdDesc(det.getOrdDesc());
        itemDTO.setProdPrice(det.getProdPrice());
        itemDTO.setProdQty(det.getProdQty());
        itemDTO.setSubtotal(det.getProdPrice() * det.getProdQty());
        return itemDTO;
    }
}
