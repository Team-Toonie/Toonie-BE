package com.example.toonieproject.entity.Books;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "series_authors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SeriesAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
}
