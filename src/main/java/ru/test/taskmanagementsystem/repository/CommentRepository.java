package ru.test.taskmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.test.taskmanagementsystem.model.entity.Comment;

import java.util.List;
import java.util.Optional;


/**
 * Репозиторий для работы с комментариями.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Получает список комментариев по ID задачи.
     *
     * @param taskId ID задачи, к которой привязаны комментарии.
     * @return Список комментариев.
     */
    List<Comment> findByTaskId(Long taskId);
}
