package com.example.MyBookShopApp.dto.cart;

import com.example.MyBookShopApp.dto.author.AuthorElementDto;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartBookElementDto {
    private String title;
    private String slug;
    private String image;
    private int price;
    private List<AuthorElementDto> authors;
    private short discount;
    private boolean isBestseller;
    private Object rating;
    private int discountPrice;
}
