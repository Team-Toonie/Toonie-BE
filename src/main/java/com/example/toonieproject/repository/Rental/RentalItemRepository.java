package com.example.toonieproject.repository.Rental;

import com.example.toonieproject.entity.Rental.RentalItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalItemRepository extends JpaRepository<RentalItem, Long> {

    // 특정 대여건에 포함된 모든 책 조회
    List<RentalItem> findByRentalId(Long rentalId);
    List<RentalItem> findByBookId(Long bookId);


}