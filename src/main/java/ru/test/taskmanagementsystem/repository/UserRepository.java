package ru.test.taskmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.taskmanagementsystem.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
