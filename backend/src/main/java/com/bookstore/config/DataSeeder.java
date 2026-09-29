package com.bookstore.config;

import com.bookstore.entity.Book;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.email:admin@bookstore.com}")
    private String adminEmail;

    @Value("${app.seed.admin.password:Admin123!}")
    private String adminPassword;

    @Value("${app.seed.admin.phone:0000000000}")
    private String adminPhone;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedBooks();
    }

    private void seedAdmin() {
        if (!userRepository.existsByEmail(adminEmail)) {
            userRepository.save(User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .phone(adminPhone)
                    .role(Role.ADMIN)
                    .build());
        }
    }

    private void seedBooks() {
        if (bookRepository.count() > 0) {
            return;
        }

        bookRepository.saveAll(List.of(
                book("The Midnight Library", "Matt Haig", "Fiction", "18.99",
                        "Between life and death there is a library, and within that library, the shelves go on forever. Every book offers a chance to try another life you could have lived.",
                        "https://covers.openlibrary.org/b/isbn/9780525559474-L.jpg"),
                book("Atomic Habits", "James Clear", "Self-Improvement", "21.50",
                        "A practical guide to building good habits, breaking bad ones, and making small changes that lead to remarkable results.",
                        "https://covers.openlibrary.org/b/isbn/9780735211292-L.jpg"),
                book("Dune", "Frank Herbert", "Science Fiction", "16.25",
                        "Set on the desert planet Arrakis, Dune is the story of Paul Atreides and a sweeping struggle over power, prophecy, and the most valuable substance in the universe.",
                        "https://covers.openlibrary.org/b/isbn/9780441172719-L.jpg"),
                book("Educated", "Tara Westover", "Memoir", "17.00",
                        "Born to survivalists in the mountains of Idaho, Tara Westover was seventeen the first time she set foot in a classroom. Her memoir is a story of education and self-invention.",
                        "https://covers.openlibrary.org/b/isbn/9780399590504-L.jpg"),
                book("Project Hail Mary", "Andy Weir", "Science Fiction", "22.00",
                        "A lone astronaut must save humanity and the Earth from extinction in this thrilling interstellar adventure.",
                        "https://covers.openlibrary.org/b/isbn/9780593135204-L.jpg"),
                book("The Very Secret Society of Irregular Witches", "Sangu Mandanna", "Fantasy", "15.75",
                        "A lonely witch finds an unexpected home, found family, and the possibility of love in a cozy and charming fantasy.",
                        "https://covers.openlibrary.org/b/isbn/9780593439357-L.jpg"),
                book("Sapiens", "Yuval Noah Harari", "History", "19.95",
                        "A sweeping account of the history of humankind, from the earliest humans to the revolutions that shaped the modern world.",
                        "https://covers.openlibrary.org/b/isbn/9780062316097-L.jpg"),
                book("The Name of the Wind", "Patrick Rothfuss", "Fantasy", "20.00",
                        "The tale of Kvothe, from his childhood among traveling players to his years at a legendary school of magic.",
                        "https://covers.openlibrary.org/b/isbn/9780756404741-L.jpg")
        ));
    }

    private Book book(String title, String author, String category, String price, String description, String imageUrl) {
        return Book.builder()
                .title(title)
                .author(author)
                .category(category)
                .price(new BigDecimal(price))
                .description(description)
                .imageUrl(imageUrl)
                .build();
    }
}
