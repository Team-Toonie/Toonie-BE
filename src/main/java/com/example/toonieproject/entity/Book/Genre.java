package com.example.toonieproject.entity.Book;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genres")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Genre {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private String name;
}
