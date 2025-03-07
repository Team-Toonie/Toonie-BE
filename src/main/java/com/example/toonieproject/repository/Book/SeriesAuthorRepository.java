package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Book.SeriesAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SeriesAuthorRepository extends JpaRepository<SeriesAuthor, Long> {

    @Query("SELECT sa.author.authorId FROM SeriesAuthor sa WHERE sa.series.seriesId = :seriesId")
    List<Long> findAuthorIdsBySeriesId(@Param("seriesId") Long seriesId);
}
