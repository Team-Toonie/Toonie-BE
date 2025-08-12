package com.example.toonieproject.dto.Series;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeriesDetailResponse {

    private Long seriesId;
    private String title;
    private List<AuthorDTO> author;
    private List<String> genre;
    private String image;
    private String publisher;

}
