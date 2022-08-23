package com.github.ivanfomkin.bookshop.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommonPageableDto<T> {
    private long total;
    private List<T> data;
    private long perPage;
    private long page;
}
