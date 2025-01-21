package com.example.toonieproject.entity.Books;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "series_genres")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SeriesGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    @ManyToOne
    @JoinColumn(name = "genre_name")
    private Genre genre;
}
