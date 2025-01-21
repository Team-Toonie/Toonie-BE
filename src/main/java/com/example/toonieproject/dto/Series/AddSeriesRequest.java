package com.example.toonieproject.dto.Series;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddSeriesRequest {

    private String title;
    private String image;
    private String publisher;
    private List<AuthorDTO> authors;
    private List<String> genres;

}

