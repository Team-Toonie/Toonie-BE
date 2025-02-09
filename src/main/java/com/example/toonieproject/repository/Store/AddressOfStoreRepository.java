package com.example.toonieproject.repository.Store;

import com.example.toonieproject.entity.Store.AddressOfStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressOfStoreRepository extends JpaRepository<AddressOfStore, Long> {


    Optional<AddressOfStore> findById(Long storeId);

}
