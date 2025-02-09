package com.example.toonieproject.repository.Store;



import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.entity.User.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByOwner_Id(Long ownerId);
}