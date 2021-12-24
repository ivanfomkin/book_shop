package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.entity.tag.TagWithWeightObject;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final TagService tagService;

    public MainPageController(BookService bookService, TagService tagService) {
        this.bookService = bookService;
        this.tagService = tagService;
    }

    @ModelAttribute("bookList")
    public BookListDto recommendedBooks() {
        return bookService.getPageableRecommendedBooks(0, 20);
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("tags")
    public List<TagWithWeightObject> tagCloud() {
        return tagService.getTagsWithWeight();
    }

    @GetMapping
    public String mainPage(Model model) {
        return "index";
    }
}
