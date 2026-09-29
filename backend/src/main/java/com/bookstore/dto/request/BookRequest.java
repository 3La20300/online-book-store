package com.bookstore.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class BookRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 160, message = "Author must not exceed 160 characters")
    private String author;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal price;

    private String description;

    @Size(max = 2048, message = "Image URL must not exceed 2048 characters")
    private String imageUrl;

    public BookRequest() {
    }

    public BookRequest(String title, String author, String category, BigDecimal price, String description, String imageUrl) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static BookRequestBuilder builder() {
        return new BookRequestBuilder();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static class BookRequestBuilder {
        private String title;
        private String author;
        private String category;
        private BigDecimal price;
        private String description;
        private String imageUrl;

        public BookRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BookRequestBuilder author(String author) {
            this.author = author;
            return this;
        }

        public BookRequestBuilder category(String category) {
            this.category = category;
            return this;
        }

        public BookRequestBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public BookRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BookRequestBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public BookRequest build() {
            return new BookRequest(title, author, category, price, description, imageUrl);
        }
    }
}
