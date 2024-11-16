package ru.test.taskmanagementsystem.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.test.taskmanagementsystem.model.enums.Role;

import java.util.Collection;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name="users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String username;

    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Override
    public String toString() {
        return email + " " + role;
    }
}
