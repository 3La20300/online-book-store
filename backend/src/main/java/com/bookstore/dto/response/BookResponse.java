package com.bookstore.dto.response;

import com.bookstore.entity.Book;

import java.math.BigDecimal;

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
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getPrice(),
                book.getDescription(),
                book.getImageUrl()
        );
    }
}
