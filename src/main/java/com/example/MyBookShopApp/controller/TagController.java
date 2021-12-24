package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tags")
public class TagController {
    private final BookService bookService;

    public TagController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @GetMapping("/{tag}")
    public String tagPage(Model model,
                          @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                          @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                          @PathVariable(required = false) String tag) {
        model.addAttribute("bookList", bookService.getPageableBooksByTag(offset, limit, tag));
        model.addAttribute("tagName", tag);
        model.addAttribute("category", "refreshid");
        return "tags/index";
    }
}
