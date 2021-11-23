package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.model.Author;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

public interface AuthorsController {
    Map<String, List<Author>> getAuthors();

    String authorsPage(Model model);
}
