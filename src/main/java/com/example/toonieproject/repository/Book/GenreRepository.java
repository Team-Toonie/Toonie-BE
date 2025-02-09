package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Book.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
    @Override
    List<Genre> findAll();

    Optional<Genre> findByName(String name);

}
