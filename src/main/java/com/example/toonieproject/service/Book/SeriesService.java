package com.example.toonieproject.service.Book;

import com.example.toonieproject.dto.Series.AddSeriesRequest;
import com.example.toonieproject.dto.Series.AuthorDTO;
import com.example.toonieproject.dto.Series.SeriesDetailResponse;
import com.example.toonieproject.entity.Book.*;
import com.example.toonieproject.repository.Book.*;
import com.example.toonieproject.service.Storage.FirebaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final SeriesAuthorRepository seriesAuthorRepository;
    private final SeriesGenreRepository seriesGenreRepository;
    private final FirebaseStorageService firebaseStorageService;



    public void add(AddSeriesRequest request, MultipartFile imageFile) {

        // 1. 시리즈 저장
        Series series = new Series();
        series.setTitle(request.getTitle());
        // 이미지 링크로 변환 후 저장
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = null;
            try {
                imageUrl = firebaseStorageService.uploadImage(imageFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            series.setImage(imageUrl);
        }
        series.setPublisher(request.getPublisher());

        seriesRepository.save(series);


        // 2. 작가 저장
        for (AuthorDTO authorDTO : request.getAuthors()) { // List 순회
            Long authorId = authorDTO.getAuthorId();
            String authorName = authorDTO.getAuthorName();
            Author author;

            if (authorId == null) { // 기존에 등록되지 않은 작가 -> 작가 등록
                author = new Author();
                author.setName(authorName);
                authorRepository.save(author);
            } else { // 기존에 등록되어있는 작가
                author = authorRepository.findById(authorId)
                        .orElseThrow(() -> new IllegalArgumentException("작가 번호 " + authorId + "를 찾을 수 없습니다."));
            }

            // 시리즈-작가 저장
            SeriesAuthor seriesAuthor = new SeriesAuthor();
            seriesAuthor.setSeries(series);
            seriesAuthor.setAuthor(author);
            seriesAuthorRepository.save(seriesAuthor);
        }


        // 3. 장르 저장
        for (String genreName : request.getGenres()) {
            // 장르가 이미 존재하는지 확인
            Genre genre = genreRepository.findByName(genreName)
                    .orElseThrow(() -> new IllegalArgumentException("장르 이름 " + genreName + "를 찾을 수 없습니다."));

            // 시리즈-장르 저장
            SeriesGenre seriesGenre = new SeriesGenre();
            seriesGenre.setSeries(series);
            seriesGenre.setGenre(genre);
            seriesGenreRepository.save(seriesGenre);
        }

    }


    public SeriesDetailResponse getSeriesDetails(Long seriesId) {

        // 1. 시리즈
        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시리즈가 존재하지 않습니다."));

        // 2.1 작가 - 시리즈 ID로 SeriesAuthor 테이블에서 authorId 리스트 가져오기
        List<Long> authorIds = seriesAuthorRepository.findAuthorIdsBySeriesId(seriesId);
        List<Author> authors = authorRepository.findAllById(authorIds);
        List<AuthorDTO> authorDTOs = authors.stream()
                .map(author -> new AuthorDTO(author.getAuthorId(), author.getName()))
                .toList();

        // 3. 장르
        List<String> genreNames = seriesGenreRepository.findGenreNamesBySeriesId(seriesId);


        return new SeriesDetailResponse(
                series.getSeriesId(),
                series.getTitle(),
                authorDTOs,
                genreNames,
                series.getImage(),
                series.getPublisher()
        );


    }
}
