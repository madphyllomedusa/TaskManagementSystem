package ru.test.taskmanagementsystem.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;

/**
 * Класс для создания спецификаций фильтрации задач в базе данных.
 */
public class TaskSpecification {

    /**
     * Фильтрует задачи по имени автора.
     *
     * @param author Имя автора задачи.
     * @return Спецификация для фильтрации задач.
     */
    public static Specification<Task> hasAuthor(String author) {
        return (root, query, criteriaBuilder) ->
                author == null ? null :
                        criteriaBuilder.equal(root.get("author").get("username"), author);
    }

    /**
     * Фильтрует задачи по имени исполнителя.
     *
     * @param assignee Имя исполнителя задачи.
     * @return Спецификация для фильтрации задач.
     */
    public static Specification<Task> hasAssignee(String assignee) {
        return (root, query, criteriaBuilder) ->
                assignee == null ? null :
                        criteriaBuilder.equal(root.get("assignee").get("username"), assignee);
    }

    /**
     * Фильтрует задачи по статусу.
     *
     * @param status Статус задачи.
     * @return Спецификация для фильтрации задач.
     */
    public static Specification<Task> hasStatus(Status status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null :
                        criteriaBuilder.equal(root.get("status"), status);
    }

    /**
     * Фильтрует задачи по приоритету.
     *
     * @param priority Приоритет задачи.
     * @return Спецификация для фильтрации задач.
     */
    public static Specification<Task> hasPriority(Priority priority) {
        return (root, query, criteriaBuilder) ->
                priority == null ? null :
                        criteriaBuilder.equal(root.get("priority"), priority);
    }

    /**
     * Фильтрует задачи по ID.
     *
     * @param id ID задачи.
     * @return Спецификация для фильтрации задач.
     */
    public static Specification<Task> hasId(Long id) {
        return (root, query, criteriaBuilder) ->
                id == null ? null :
                        criteriaBuilder.equal(root.get("id"), id);
    }
}
