package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Books.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, String> {
    @Override
    List<Genre> findAll();

    Optional<Genre> findByName(String name);

}
