package com.foodietime.orders.controller;

import com.foodietime.member.model.MemberVO;
import com.foodietime.orders.model.OrdersService;
import com.foodietime.orders.model.OrdersVO;
import com.foodietime.store.model.StoreVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/store/orders")
public class MerchantOrdersController {

    private final OrdersService ordersService;

    @Autowired
    public MerchantOrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    /**
     * 顯示商家訂單管理頁面。
     * 查詢當前登入商家的所有訂單並按狀態分類顯示。
     *
     * @param model   參數用途：將訂單數據傳遞到視圖層。
     *                資料來源：Spring MVC 提供的 Model 物件。
     * @param session 參數用途：獲取當前登入的商家資訊。
     *                資料來源：HTTP Session 中儲存的登入狀態。
     * @param statusFilter 參數用途：訂單狀態篩選參數，預設為"all"顯示所有訂單。
     *                     資料來源：URL查詢參數。
     * @return String 參數用途：返回商家訂單管理頁面的視圖路徑。
     */
    @GetMapping("/manage")
    public String showMerchantOrders(Model model,
                                     HttpSession session,
                                     @RequestParam(value = "status", defaultValue = "all") String statusFilter) {

        // ================== 步驟1：驗證商家登入狀態 ==================
        MemberVO currentStore = (MemberVO) session.getAttribute("loggedInMember");
        if (currentStore == null) {
            return "redirect:/login";
        }

        // ================== 步驟2：查詢該商家的所有訂單 ==================
        List<OrdersVO> allOrders = ordersService.getOrdersByStoreId(currentStore.getMemId());

        // ================== 步驟3：根據狀態篩選訂單 ==================
        List<OrdersVO> filteredOrders = filterOrdersByStatus(allOrders, statusFilter);

        // ================== 步驟4：統計各狀態訂單數量 ==================
        OrderStatusCount statusCount = calculateStatusCount(allOrders);

        // ================== 步驟5：將數據傳遞給前端 ==================
        model.addAttribute("orders", filteredOrders);
        model.addAttribute("statusCount", statusCount);
        model.addAttribute("currentFilter", statusFilter);
        model.addAttribute("store", currentStore);

        return "/front/merchantorders/merchant-orders";
    }

    /**
     * 處理商家接受訂單的請求。
     *
     * @param orderId             參數用途：要接受的訂單ID。
     *                           資料來源：前端表單提交的訂單ID。
     * @param session            參數用途：獲取當前登入商家資訊進行權限驗證。
     *                           資料來源：HTTP Session。
     * @param redirectAttributes 參數用途：重定向時傳遞成功或失敗訊息。
     *                           資料來源：Spring MVC 提供的重定向屬性。
     * @return String            參數用途：重定向到訂單管理頁面。
     */
    @PostMapping("/accept/{orderId}")
    public String acceptOrder(@PathVariable Integer orderId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        // ================== 步驟1：安全性檢查 ==================
        MemberVO currentStore = (MemberVO) session.getAttribute("loggedInMember");
        if (currentStore == null) {
            return "redirect:/login";
        }

        try {
            // ================== 步驟2：驗證訂單歸屬權 ==================
            if (!ordersService.validateOrderBelongsToStore(orderId, currentStore.getMemId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "您沒有權限操作此訂單！");
                return "redirect:/merchant/orders/manage";
            }

            // ================== 步驟3：更新訂單狀態為已接單 ==================
            ordersService.updateOrderStatus(orderId, 1); // 1 = 已接單

            redirectAttributes.addFlashAttribute("successMessage", "訂單已成功接受！");

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "接受訂單時發生錯誤，請稍後再試。");
        }

        return "redirect:/merchant/orders/manage";
    }

    /**
     * 處理商家拒絕訂單的請求。
     *
     * @param orderId        參數用途：要拒絕的訂單ID。
     *                      資料來源：前端表單提交的訂單ID。
     * @param cancelReason   參數用途：拒絕訂單的原因。
     *                      資料來源：前端表單提交的拒絕原因。
     * @param customReason   參數用途：自訂拒絕原因（當選擇"其他"時）。
     *                      資料來源：前端表單的自訂原因輸入框。
     * @param session        參數用途：獲取當前登入商家進行權限驗證。
     * @param redirectAttributes 參數用途：重定向時傳遞訊息。
     * @return String        參數用途：重定向到訂單管理頁面。
     */
    @PostMapping("/reject/{orderId}")
    public String rejectOrder(@PathVariable Integer orderId,
                              @RequestParam("cancelReason") String cancelReason,
                              @RequestParam(value = "customReason", required = false) String customReason,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        // ================== 步驟1：安全性檢查 ==================
        MemberVO currentStore = (MemberVO) session.getAttribute("loggedInMember");
        if (currentStore == null) {
            return "redirect:/login";
        }

        try {
            // ================== 步驟2：驗證訂單歸屬權 ==================
            if (!ordersService.validateOrderBelongsToStore(orderId, currentStore.getMemId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "您沒有權限操作此訂單！");
                return "redirect:/merchant/orders/manage";
            }

            // ================== 步驟3：處理拒絕原因 ==================
            String finalCancelReason = "其他".equals(cancelReason) && customReason != null
                    ? customReason.trim()
                    : cancelReason;

            if (finalCancelReason == null || finalCancelReason.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "請提供拒絕訂單的原因！");
                return "redirect:/merchant/orders/manage";
            }

            // ================== 步驟4：取消訂單 ==================
            ordersService.cancelOrder(orderId, finalCancelReason);

            redirectAttributes.addFlashAttribute("successMessage", "訂單已拒絕，原因：" + finalCancelReason);

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "拒絕訂單時發生錯誤，請稍後再試。");
        }

        return "redirect:/merchant/orders/manage";
    }

    /**
     * 處理商家完成訂單的請求。
     *
     * @param orderId             參數用途：要完成的訂單ID。
     *                           資料來源：前端表單提交的訂單ID。
     * @param session            參數用途：獲取當前登入商家進行權限驗證。
     * @param redirectAttributes 參數用途：重定向時傳遞訊息。
     * @return String            參數用途：重定向到訂單管理頁面。
     */
    @PostMapping("/complete/{orderId}")
    public String completeOrder(@PathVariable Integer orderId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        // ================== 步驟1：安全性檢查 ==================
        MemberVO currentStore = (MemberVO) session.getAttribute("loggedInMember");
        if (currentStore == null) {
            return "redirect:/login";
        }

        try {
            // ================== 步驟2：驗證訂單歸屬權 ==================
            if (!ordersService.validateOrderBelongsToStore(orderId, currentStore.getMemId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "您沒有權限操作此訂單！");
                return "redirect:/merchant/orders/manage";
            }

            // ================== 步驟3：更新訂單狀態為已完成 ==================
            ordersService.updateOrderStatus(orderId, 3); // 3 = 已完成

            redirectAttributes.addFlashAttribute("successMessage", "訂單已標記為完成！");

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "完成訂單時發生錯誤，請稍後再試。");
        }

        return "redirect:/merchant/orders/manage";
    }

    // ================== 輔助方法 ==================

    /**
     * 根據狀態篩選訂單列表。
     */
    private List<OrdersVO> filterOrdersByStatus(List<OrdersVO> orders, String statusFilter) {
        if ("all".equals(statusFilter)) {
            return orders;
        }

        return orders.stream()
                .filter(order -> matchesStatusFilter(order.getOrderStatus(), statusFilter))
                .toList();
    }

    /**
     * 判斷訂單狀態是否符合篩選條件。
     */
    private boolean matchesStatusFilter(Integer orderStatus, String statusFilter) {
        return switch (statusFilter) {
            case "pending" -> orderStatus == 0;    // 待處理
            case "accepted" -> orderStatus == 1;   // 已接單
            case "preparing" -> orderStatus == 2;  // 準備中
            case "completed" -> orderStatus == 3;  // 已完成
            case "cancelled" -> orderStatus == 99; // 已取消
            default -> true;
        };
    }

    /**
     * 計算各狀態訂單數量。
     */
    private OrderStatusCount calculateStatusCount(List<OrdersVO> orders) {
        OrderStatusCount count = new OrderStatusCount();
        for (OrdersVO order : orders) {
            switch (order.getOrderStatus()) {
                case 0 -> count.pending++;
                case 1 -> count.accepted++;
                case 2 -> count.preparing++;
                case 3 -> count.completed++;
                case 99 -> count.cancelled++;
            }
        }
        return count;
    }

    /**
     * 內部類別：訂單狀態統計
     */
    public static class OrderStatusCount {
        public int pending = 0;
        public int accepted = 0;
        public int preparing = 0;
        public int completed = 0;
        public int cancelled = 0;
    }
}
