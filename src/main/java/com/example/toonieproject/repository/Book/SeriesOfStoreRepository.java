package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Book.SeriesOfStore;
import com.example.toonieproject.entity.Book.SeriesOfStoreId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesOfStoreRepository extends JpaRepository<SeriesOfStore, SeriesOfStoreId> {
}
