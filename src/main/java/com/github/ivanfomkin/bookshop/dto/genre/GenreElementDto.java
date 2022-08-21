package com.github.ivanfomkin.bookshop.dto.genre;

import com.github.ivanfomkin.bookshop.entity.genre.GenreEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenreElementDto {
    public GenreElementDto(GenreEntity genreEntity) {
        this.genreName = genreEntity.getName();
        this.slug = genreEntity.getSlug();
    }

    private String genreName;
    private String slug;
}
