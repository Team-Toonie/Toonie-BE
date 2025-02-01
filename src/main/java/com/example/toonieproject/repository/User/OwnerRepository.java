package com.example.toonieproject.repository.User;

import com.example.toonieproject.entity.User.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}
