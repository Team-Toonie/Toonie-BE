package com.example.toonieproject.repository.Store;

import com.example.toonieproject.entity.Store.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByStoreId(Long storeId);
    Page<Notice> findByStoreId(Long storeId, Pageable pageable);
}
