package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Book.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {



}
