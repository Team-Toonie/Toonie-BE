package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Book.Series;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {



}