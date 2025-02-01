package com.example.toonieproject.repository.Store;



import com.example.toonieproject.entity.Store.Store;
import com.example.toonieproject.entity.User.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Store findByOwner(Owner owner);
}