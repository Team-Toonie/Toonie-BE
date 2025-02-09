package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Book.SeriesOfStore;
import com.example.toonieproject.entity.Book.SeriesOfStoreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesOfStoreRepository extends JpaRepository<SeriesOfStore, SeriesOfStoreId> {
}
