package com.foodietime.cart.controller;

import com.foodietime.cart.dto.CartItemDTO;
import com.foodietime.cart.model.CartService;
import com.foodietime.member.model.MemberVO;
import com.foodietime.store.model.StoreVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

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
            return "redirect:/front/member/login"; // 導向登入頁面
        }
        Integer currentMemberId = memberVO.getMemId();

        try {
            // 步驟1：從 Redis 獲取購物車列表
            List<CartItemDTO> cartList = cartService.getCart(currentMemberId);

            if (!cartList.isEmpty()) {
                // 步驟2：按店家分組
                Map<StoreVO, List<CartItemDTO>> groupedCartData = cartList.stream()
                        .collect(Collectors.groupingBy(
                                cartItem -> cartItem.getProduct().getStore(),
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));
                model.addAttribute("groupedCartData", groupedCartData);

                // 步驟3：計算總金額和總數量
                Integer totalAmount = cartList.stream()
                        .mapToInt(item -> item.getProduct().getProdPrice() * item.getQuantity())
                        .sum();
                model.addAttribute("totalAmount", totalAmount);

                Integer totalQuantity = cartList.stream()
                        .mapToInt(CartItemDTO::getQuantity)
                        .sum();
                model.addAttribute("totalQuantity", totalQuantity);

                // 步驟4：計算運費和最終總價
                int shippingFee = totalAmount >= 500 ? 0 : 60;
                model.addAttribute("shippingFee", shippingFee);
                model.addAttribute("finalTotal", totalAmount + shippingFee);
            } else {
                // 處理空購物車
                model.addAttribute("groupedCartData", new LinkedHashMap<>());
                model.addAttribute("totalAmount", 0);
                model.addAttribute("totalQuantity", 0);
                model.addAttribute("shippingFee", 0);
                model.addAttribute("finalTotal", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "載入購物車時發生無法預期的錯誤：" + e.getMessage());
            model.addAttribute("groupedCartData", new LinkedHashMap<>());
        }
        return "front/cart/cart";
    }

    /**
     * 【已修改】刪除購物車商品，現在使用 prodId
     */
    @PostMapping("/delete")
    public String deleteCartItem(@RequestParam Integer prodId, HttpSession session) {
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
        if (memberVO != null) {
            cartService.deleteCartItem(memberVO.getMemId(), prodId);
        }
        return "redirect:/cart/cart";
    }

    /**
     * 【已修改】更新商品數量，現在使用 prodId
     */
    @PostMapping("/updateQuantity")
    public String updateCartQuantity(@RequestParam Integer prodId,
                                     @RequestParam Integer newQuantity,
                                     HttpSession session) {
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");
        if (memberVO != null) {
            cartService.updateCartQuantity(memberVO.getMemId(), prodId, newQuantity);
        }
        return "redirect:/cart/cart";
    }

    /**
     * 【已修改】異步加入購物車API
     */
    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCartAPI(
            @RequestParam Integer prodId,
            @RequestParam Integer quantity,
            HttpSession session) {

        Map<String, Object> response = new LinkedHashMap<>();
        MemberVO memberVO = (MemberVO) session.getAttribute("loggedInMember");

        if (memberVO == null) {
            response.put("success", false);
            response.put("message", "請先登入會員");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            cartService.addCart(memberVO.getMemId(), prodId, quantity);
            Integer totalItems = cartService.getCartItemCount(memberVO.getMemId());

            response.put("success", true);
            response.put("message", "已成功加入購物車！");
            response.put("cartItemCount", totalItems);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "加入購物車失敗，請稍後再試。");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}