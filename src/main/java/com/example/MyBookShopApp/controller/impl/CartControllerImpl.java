package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.CartController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartControllerImpl implements CartController {
    @GetMapping
    public String cartPage() {
        return "cart";
    }
}
