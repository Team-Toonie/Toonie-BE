package com.example.toonieproject.entity.Books;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "series")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
//@ToString(exclude = {"seriesAuthors", "seriesGenres"})
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seriesId;

    @Column(nullable = false)
    private String title;

    @Column
    private String image;

    @Column
    private String publisher;


}


