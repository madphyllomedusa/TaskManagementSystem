package ru.test.taskmanagementsystem.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;


/**
 * Сущность комментария.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    /**
     * Уникальный идентификатор комментария.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Автор комментария (связь с пользователем).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Текст комментария.
     */
    @Column(nullable = false)
    private String text;

    /**
     * Дата и время создания комментария.
     */
    @CreationTimestamp
    private OffsetDateTime createdAt;

    /**
     * Дата и время последнего обновления комментария.
     */
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    /**
     * Дата и время удаления комментария (если применимо).
     */
    private OffsetDateTime deletedAt;

    /**
     * Связанная задача (связь с задачей).
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
