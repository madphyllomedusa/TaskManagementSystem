package ru.test.taskmanagementsystem.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

/**
 * Сущность задачи.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tasks")
public class Task {
    /**
     * Уникальный идентификатор задачи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Заголовок задачи.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Описание задачи.
     */
    @Column(nullable = false)
    private String description;

    /**
     * Статус задачи.
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Приоритет задачи.
     */
    @Enumerated(EnumType.STRING)
    private Priority priority;

    /**
     * Дата и время создания задачи.
     */
    @CreationTimestamp
    private OffsetDateTime createdAt;

    /**
     * Дата и время последнего обновления задачи.
     */
    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    /**
     * Дата и время закрытия задачи.
     */
    private OffsetDateTime closedAt;

    /**
     * Дата и время удаления задачи (если применимо).
     */
    private OffsetDateTime deletedAt;

    /**
     * Дата и время дедлайна задачи.
     */
    private OffsetDateTime deadlineAt;

    /**
     * Список комментариев, привязанных к задаче.
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    /**
     * Автор задачи.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * Назначенный исполнитель задачи.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;
}
