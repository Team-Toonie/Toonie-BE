package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Books.SeriesGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  SeriesGenreRepository extends JpaRepository<SeriesGenre, Long> {
}
