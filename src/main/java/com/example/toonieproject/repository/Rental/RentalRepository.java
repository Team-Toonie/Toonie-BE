package com.example.toonieproject.repository.Rental;


import com.example.toonieproject.dto.Rental.Reservation.AddRentalRequest;
import com.example.toonieproject.entity.Rental.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    // 유저의 대여 내역 조회
    List<Rental> findByUserId(Long userId);

    // 특정 가게의 대여 내역 조회
    List<Rental> findByStoreId(Long storeId);

    Page<Rental> findByUserIdOrderByReservedAtDesc(Long userId, Pageable pageable);

    Page<Rental> findByRentalIdIn(List<Long> rentalIds, Pageable pageable);


}
