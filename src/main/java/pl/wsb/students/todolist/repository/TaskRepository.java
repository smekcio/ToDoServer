package pl.wsb.students.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wsb.students.todolist.models.Task;
import pl.wsb.students.todolist.models.TaskList;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<List<Task>> findByTaskList(TaskList taskList);
    Optional<List<Task>> findByTaskListId(Long taskListId);
    Optional<List<Task>> findByTaskListIdAndVisible(Long taskListId, Boolean visible);
    Optional<Long> countByTaskListIdAndVisible(Long taskListId, Boolean visible);
}
