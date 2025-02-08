package ru.test.taskmanagementsystem.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import ru.test.taskmanagementsystem.model.enums.Role;

import java.time.OffsetDateTime;


/**
 * Сущность пользователя.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя (уникальное).
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Email пользователя (уникальный).
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Пароль пользователя (хранится в зашифрованном виде).
     */
    @Column(nullable = false)
    private byte[] password;

    /**
     * Роль пользователя (ROLE_USER или ROLE_ADMIN).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Дата и время регистрации пользователя.
     */
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Override
    public String toString() {
        return username + " " + role;
    }
}
