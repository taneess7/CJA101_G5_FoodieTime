package com.foodietime.cart.controller;

import com.foodietime.cart.model.CartService;
import com.foodietime.cart.model.CartVO;
import com.foodietime.member.model.MemberVO;
import com.foodietime.store.model.StoreVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
public class CartController {
    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {this.cartService = cartService;}

    @GetMapping("login")
    public String login() {
        return "front/member/login";
    }

    @GetMapping("checkout")
    public String checkout(Model model) {
        return "front/cart/checkout";
    }

    @GetMapping("order-confirmation")
    public String orderConfirmation(Model model) {
        return "front/cart/order-confirmation";
    }

    @GetMapping("cart")
    public String listAllCart(Model model, HttpSession session) {
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
        if (memberVO == null) {
            return "redirect:/cart/login";
        }
        Integer currentMemberId = memberVO.getMemId();

        try {
            // 步驟1：獲取包含完整資訊的購物車列表 (這一步是正確的)
            List<CartVO> cartList = cartService.getByMemId(currentMemberId);

            // ==================== 核心修正點：直接基於 cartList 計算 ====================
            if (!cartList.isEmpty()) {
                // 步驟2：直接使用已獲取的 cartList 進行分組
                Map<StoreVO, List<CartVO>> groupedCartData = cartList.stream()
                        .collect(Collectors.groupingBy(
                                cartVO -> cartVO.getProduct().getStore(),
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));
                model.addAttribute("groupedCartData", groupedCartData);

                // 步驟3：直接基於已獲取的 cartList 計算總金額和總數量，不再呼叫 Service
                Integer totalAmount = cartList.stream()
                        .mapToInt(cart -> cart.getProduct().getProdPrice() * cart.getProdN())
                        .sum();
                model.addAttribute("totalAmount", totalAmount);

                Integer totalQuantity = cartList.stream()
                        .mapToInt(CartVO::getProdN)
                        .sum();
                model.addAttribute("totalQuantity", totalQuantity);

                // 步驟4：計算運費和最終總價
                Integer shippingFee = totalAmount >= 500 ? 0 : 60;
                model.addAttribute("shippingFee", shippingFee);

                Integer finalTotal = totalAmount + shippingFee;
                model.addAttribute("finalTotal", finalTotal);
            } else {
                // 處理空購物車
                model.addAttribute("groupedCartData", new LinkedHashMap<>());
                model.addAttribute("totalAmount", 0);
                model.addAttribute("totalQuantity", 0);
                model.addAttribute("shippingFee", 0);
                model.addAttribute("finalTotal", 0);
            }
            // ======================================================================

        } catch (Exception e) {
            // 為了除錯
            e.printStackTrace();
            model.addAttribute("error", "載入購物車時發生無法預期的錯誤：" + e.getMessage());
            model.addAttribute("groupedCartData", new LinkedHashMap<>());
        }
        return "front/cart/cart";
    }

    @PostMapping("/delete")
    public String deleteCartItem(@RequestParam Integer shopId) {
        try {
            // ================== 刪除購物車商品 ==================
            cartService.deleteCart(shopId);

        } catch (Exception e) {
            // 可以加入錯誤處理邏輯
            System.out.println("刪除購物車商品失敗：" + e.getMessage());
        }

        return "redirect:/cart/cart";
    }

    @PostMapping("/updateQuantity")
    public String updateCartQuantity(@RequestParam Integer shopId,
                                     @RequestParam Integer newQuantity,
                                     HttpSession session) {
        try {
            // ================== 步驟1：獲取當前會員ID ==================
//            Integer currentMemberId = 1; // 測試用，實際應從Session獲取
            // 步驟1：從 session 中取出完整的 MemberVO 物件
            MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");

            // 步驟2：從 MemberVO 物件中取得會員 ID
            Integer currentMemberId = memberVO.getMemId(); // 假設 getter 方法是 getMemId()

            // ================== 步驟2：更新商品數量 ==================
            cartService.updateCart(shopId, currentMemberId, newQuantity);

        } catch (Exception e) {
            // 可以加入錯誤處理邏輯
            System.out.println("更新購物車失敗：" + e.getMessage());
        }

        return "redirect:/cart/cart";
    }


}
