package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {
    private final BookService bookService;

    public SearchController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("searchResults")
    public BookListDto searchResults() {
        return new BookListDto();
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String searchPage(Model model,
                             @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                             @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                             @PathVariable(name = "searchWord", required = false) SearchDto searchDto) {

        model.addAttribute("searchDto", searchDto);
        model.addAttribute("bookList", bookService.getPageableBooksByTitle(offset, limit, searchDto.searchQuery()));
        return "search/index";
    }
}
