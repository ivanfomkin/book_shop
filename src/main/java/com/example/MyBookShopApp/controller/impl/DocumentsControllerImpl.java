package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.DocumentsController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocumentsControllerImpl implements DocumentsController {
    @GetMapping("/documents")
    public String documentPage() {
        return "documents/index";
    }
}
