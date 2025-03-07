package com.example.toonieproject.repository.Book;

import com.example.toonieproject.entity.Book.SeriesGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  SeriesGenreRepository extends JpaRepository<SeriesGenre, Long> {

    @Query("SELECT sg.genre.name FROM SeriesGenre sg WHERE sg.series.seriesId = :seriesId")
    List<String> findGenreNamesBySeriesId(@Param("seriesId") Long seriesId);
}
