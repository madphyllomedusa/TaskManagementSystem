package ru.test.taskmanagementsystem.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.test.taskmanagementsystem.model.entity.Task;
import ru.test.taskmanagementsystem.model.enums.Priority;
import ru.test.taskmanagementsystem.model.enums.Status;

public class TaskSpecification {
    public static Specification<Task> hasAuthor(String author) {
        return (root, query, criteriaBuilder) ->
                author == null ? null :
                        criteriaBuilder.equal(root.get("author").get("username"), author);
    }

    public static Specification<Task> hasAssignee(String assignee) {
        return (root, query, criteriaBuilder) ->
                assignee == null ? null :
                        criteriaBuilder.equal(root.get("assignee").get("username"), assignee);
    }

    public static Specification<Task> hasStatus(Status status) {
        return (root, query, criteriaBuilder) ->
                status == null ?  null :
                        criteriaBuilder.equal(root.get("status"), status);
    }
    public static Specification<Task> hasPriority(Priority priority) {
        return (root, query, criteriaBuilder) ->
                priority == null ?  null :
                        criteriaBuilder.equal(root.get("priority"), priority);
    }
}
