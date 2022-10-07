package com.github.ivanfomkin.bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonPageableDto<T> {
    private long total;
    private List<T> data;
    private long perPage;
    private long page;
}
