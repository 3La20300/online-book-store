package com.bookstore.dto.response;

import com.bookstore.entity.Book;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BookResponse(
        Long id,
        String title,
        String author,
        String category,
        BigDecimal price,
        String description,
        String imageUrl
) {
    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .category(book.getCategory())
                .price(book.getPrice())
                .description(book.getDescription())
                .imageUrl(book.getImageUrl())
                .build();
    }
}
