package com.example.toonieproject.repository.User;

import com.example.toonieproject.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
