package com.foodietime.grouporders.controller;

import com.foodietime.grouporders.model.GroupOrdersService;
import com.foodietime.grouporders.model.GroupOrdersVO;
import com.foodietime.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/gb")
public class GroupOrdersController {

    @Autowired
    private GroupOrdersService groupOrdersService;

    // 查詢團長開團的所有訂單
    @GetMapping("/leader-orders")
    public String leaderOrders(HttpSession session, Model model) {
        // 檢查是否登入
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "front/member/login";
        }

        // 取得當前會員 ID
        Integer memId = member.getMemId();

        // 撈出該會員作為團長的所有訂單
        List<GroupOrdersVO> orders = groupOrdersService.getOrdersForLeader(memId);
        model.addAttribute("orders", orders);

        return "front/gb/gbleader/leader-orders";
    }
    
    /**
     * 訂單詳情頁（只能看自己做為團長的訂單）
     */
    @GetMapping("/order/{orderId}/detail")
    public String showLeaderOrderDetail(
            @PathVariable Integer orderId,
            HttpSession session,
            Model model) {

        // 1) 檢查是否登入
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "front/member/login";
        }

        // 2) 取得該筆訂單，若無則抛例外
        Integer memId = member.getMemId();
        GroupOrdersVO order = groupOrdersService.getLeaderOrder(memId, orderId);

        // 3) 放入 Model，交給 Thymeleaf 渲染
        model.addAttribute("order", order);
        return "front/gb/gbleader/leader-gborder-detail";
    }
    
    
    // 查詢某會員的所有團購訂單
    @GetMapping("/member/{memId}")
    public String getOrdersByMemberId(@PathVariable Integer memId, Model model) {
        List<GroupOrdersVO> orders = groupOrdersService.getOrdersByMemberId(memId);
        model.addAttribute("orders", orders);
        return "group-orders/group-orders-list"; // 返回訂單列表頁面
    }

    // 根據訂單狀態查詢團購訂單
    @GetMapping("/status/{orderStatus}")
    public String getOrdersByStatus(@PathVariable Byte orderStatus, Model model) {
        List<GroupOrdersVO> orders = groupOrdersService.getOrdersByStatus(orderStatus);
        model.addAttribute("orders", orders);
        return "group-orders/group-orders-list"; // 返回訂單列表頁面
    }

    // 根據付款狀態查詢團購訂單
    @GetMapping("/paymentStatus/{paymentStatus}")
    public String getOrdersByPaymentStatus(@PathVariable Byte paymentStatus, Model model) {
        List<GroupOrdersVO> orders = groupOrdersService.getOrdersByPaymentStatus(paymentStatus);
        model.addAttribute("orders", orders);
        return "group-orders/group-orders-list"; // 返回訂單列表頁面
    }

    // 根據出貨狀態查詢團購訂單
    @GetMapping("/shippingStatus/{shippingStatus}")
    public String getOrdersByShippingStatus(@PathVariable Byte shippingStatus, Model model) {
        List<GroupOrdersVO> orders = groupOrdersService.getOrdersByShippingStatus(shippingStatus);
        model.addAttribute("orders", orders);
        return "group-orders/group-orders-list"; // 返回訂單列表頁面
    }

    // 查詢某一團購的所有團購訂單
    @GetMapping("/groupBuyingCase/{gbId}")
    public String getOrdersByGroupBuyingCaseId(@PathVariable Integer gbId, Model model) {
        List<GroupOrdersVO> orders = groupOrdersService.getOrdersByGroupBuyingCaseId(gbId);
        model.addAttribute("orders", orders);
        return "group-orders/group-orders-list"; // 返回訂單列表頁面
    }

    // 根據商品編號查詢訂單
    @GetMapping("/product/{gbProdId}")
    public String getOrdersByProductId(@PathVariable Integer gbProdId, Model model) {
        List<GroupOrdersVO> orders = groupOrdersService.getOrdersByProductId(gbProdId);
        model.addAttribute("orders", orders);
        return "group-orders/group-orders-list"; // 返回訂單列表頁面
    }

    // 查詢某一店家的所有團購訂單
    @GetMapping("/store/{storId}")
    public String getOrdersByStoreId(@PathVariable Integer storId, Model model) {
        List<GroupOrdersVO> orders = groupOrdersService.getOrdersByStoreId(storId);
        model.addAttribute("orders", orders);
        return "group-orders/group-orders-list"; // 返回訂單列表頁面
    }

    // 根據訂單編號查詢訂單詳情
    @GetMapping("/order/{gbOrId}")
    public String getOrderById(@PathVariable Integer gbOrId, Model model) {
        Optional<GroupOrdersVO> order = groupOrdersService.getOrderById(gbOrId);
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
        } else {
            model.addAttribute("error", "訂單不存在");
        }
        return "group-orders/group-order-detail"; // 返回訂單詳情頁面
    }

    // 更新訂單狀態（包括訂單狀態、付款狀態和出貨狀態）
    @PostMapping("/update/{gbOrId}")
    public String updateOrderField(@PathVariable Integer gbOrId, @RequestParam String field,
                                   @RequestParam Byte newStatus, Model model) {
        // 這裡不需要try-catch，異常處理交給 AOP
        GroupOrdersVO updatedOrder = groupOrdersService.updateOrderField(gbOrId, field, newStatus);
        model.addAttribute("order", updatedOrder);
        model.addAttribute("message", "訂單更新成功");
        return "group-orders/group-order-detail"; // 返回訂單詳情頁面
    }
}