package pl.wsb.students.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wsb.students.todolist.models.TaskList;
import pl.wsb.students.todolist.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    Optional<List<TaskList>> findByOwnerAndVisible(User owner, Boolean visible);
    Boolean existsByOwner(User owner);
    Optional<TaskList> findById(Long id);
}
