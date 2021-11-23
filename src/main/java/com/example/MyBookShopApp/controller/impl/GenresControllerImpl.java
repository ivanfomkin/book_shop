package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.GenresController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/genres")
public class GenresControllerImpl implements GenresController {

    @GetMapping
    public String genresPage() {
        return "genres/index";
    }

    @GetMapping("/slug")
    public String booksByGenre() {
        return "genres/index";
    }
}
