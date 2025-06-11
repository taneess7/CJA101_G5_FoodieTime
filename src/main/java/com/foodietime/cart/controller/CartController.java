package com.foodietime.cart.controller;

import com.foodietime.cart.model.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartController {
    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {this.cartService = cartService;}

    @GetMapping("cart")
    public String cart(Model model) {
        return "cart";
    }

}
