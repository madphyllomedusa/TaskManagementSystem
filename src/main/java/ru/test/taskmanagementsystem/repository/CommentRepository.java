package ru.test.taskmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.taskmanagementsystem.model.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
