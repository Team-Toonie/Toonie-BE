package com.example.toonieproject.repository.Rental;

import com.example.toonieproject.entity.Book.Book;
import com.example.toonieproject.entity.Rental.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    void deleteByUserIdAndStoreIdAndBook(Long userId, Long storeId, Book book);
    Optional<Cart> findByUserIdAndStoreIdAndBook(Long userId, Long storeId, Book book);

    @Query("SELECT c.book.id FROM Cart c WHERE c.userId = :userId")
    List<Long> findBookIdsByUserId(@Param("userId") Long userId);

    List<Cart> findByUserIdAndBook_IsRentable(Long userId, Boolean isRentable);

}
