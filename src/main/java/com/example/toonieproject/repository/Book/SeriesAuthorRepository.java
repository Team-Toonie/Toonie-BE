package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Books.SeriesAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SeriesAuthorRepository extends JpaRepository<SeriesAuthor, Long> {


}
