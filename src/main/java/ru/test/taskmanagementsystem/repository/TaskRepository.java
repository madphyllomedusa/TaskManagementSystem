package ru.test.taskmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.test.taskmanagementsystem.model.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
