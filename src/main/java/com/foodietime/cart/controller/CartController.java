package com.foodietime.cart.controller;

import com.foodietime.cart.model.CartService;
import com.foodietime.cart.model.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {this.cartService = cartService;}

//    @GetMapping("cart")
//    public String cart(Model model) {
//        return "front/cart/cart";
//    }

    @GetMapping("checkout")
    public String checkout(Model model) {
        return "front/cart/checkout";
    }

    @GetMapping("order-confirmation")
    public String orderConfirmation(Model model) {
        return "front/cart/order-confirmation";
    }

    @GetMapping("cart")
    public String listAllCart(Model model) {
        // ================== 步驟1：獲取當前會員ID ==================
        // 注意：實際應用中應從Session或安全上下文獲取
        Integer currentMemberId = 1; // 測試用

        try {
            // ================== 步驟2：獲取購物車數據 ==================
            List<CartVO> cartList = cartService.getByMemId(currentMemberId);
            model.addAttribute("cartListData", cartList);

            // ================== 步驟3：計算購物車統計信息 ==================
            if (!cartList.isEmpty()) {
                // 計算總金額
                Integer totalAmount = cartService.calculateTotalAmount(currentMemberId);
                model.addAttribute("totalAmount", totalAmount);

                // 計算總商品數量
                Integer totalQuantity = cartService.calculateTotalQuantity(currentMemberId);
                model.addAttribute("totalQuantity", totalQuantity);

                // 計算運費邏輯
                Integer shippingFee = totalAmount >= 500 ? 0 : 60;
                model.addAttribute("shippingFee", shippingFee);

                // 計算最終總價
                Integer finalTotal = totalAmount + shippingFee;
                model.addAttribute("finalTotal", finalTotal);
            } else {
                // ================== 步驟4：處理空購物車 ==================
                model.addAttribute("totalAmount", 0);
                model.addAttribute("totalQuantity", 0);
                model.addAttribute("shippingFee", 0);
                model.addAttribute("finalTotal", 0);
            }

        } catch (Exception e) {
            // ================== 步驟5：異常處理 ==================
            model.addAttribute("error", "載入購物車時發生錯誤：" + e.getMessage());
            model.addAttribute("cartListData", new ArrayList<>());
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
                                     @RequestParam Integer newQuantity) {
        try {
            // ================== 步驟1：獲取當前會員ID ==================
            Integer currentMemberId = 1; // 測試用，實際應從Session獲取

            // ================== 步驟2：更新商品數量 ==================
            cartService.updateCart(shopId, currentMemberId, newQuantity);

        } catch (Exception e) {
            // 可以加入錯誤處理邏輯
            System.out.println("更新購物車失敗：" + e.getMessage());
        }

        return "redirect:/cart/cart";
    }


}
