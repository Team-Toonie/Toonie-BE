package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Book.Series;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {

    @Query("""
        SELECT s FROM Series s
        WHERE s.seriesId NOT IN (
            SELECT ss.series.seriesId FROM SeriesOfStore ss WHERE ss.store.id = :storeId
        )
    """)
    List<Series> findSeriesNotInStore(@Param("storeId") Long storeId);

}