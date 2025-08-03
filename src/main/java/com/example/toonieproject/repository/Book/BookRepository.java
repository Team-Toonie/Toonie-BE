package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    int countByStoreIdAndSeries_SeriesId(Long storeId, Long seriesId);
    List<Book> findBySeries_SeriesIdAndStore_Id(Long seriesId, Long storeId);


}
