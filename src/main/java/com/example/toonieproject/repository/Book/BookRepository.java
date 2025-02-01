package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {



}
