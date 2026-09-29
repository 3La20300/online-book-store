package com.bookstore.service;

import com.bookstore.dto.request.BookRequest;
import com.bookstore.dto.response.BookResponse;
import com.bookstore.entity.Book;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(BookResponse::from).toList();
    }

    public BookResponse getBookById(Long id) {
        return BookResponse.from(findBook(id));
    }

    @Transactional
    public BookResponse createBook(BookRequest request) {
        return BookResponse.from(bookRepository.save(applyRequest(new Book(), request)));
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findBook(id);
        return BookResponse.from(bookRepository.save(applyRequest(book, request)));
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.delete(findBook(id));
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    private Book applyRequest(Book book, BookRequest request) {
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setCategory(request.getCategory().trim());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription() == null ? null : request.getDescription().trim());
        book.setImageUrl(request.getImageUrl() == null || request.getImageUrl().isBlank()
                ? null
                : request.getImageUrl().trim());
        return book;
    }
}
