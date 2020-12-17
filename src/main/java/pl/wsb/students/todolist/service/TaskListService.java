package pl.wsb.students.todolist.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.wsb.students.todolist.models.Task;
import pl.wsb.students.todolist.models.TaskList;
import pl.wsb.students.todolist.models.User;
import pl.wsb.students.todolist.repository.TaskListRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class TaskListService {
    @Autowired
    private final TaskListRepository taskListRepository;

    @Autowired
    private UserService userService;

    @Value("${todo.app.softdelete}")
    private Boolean softDelete;

    private static final Logger logger = LoggerFactory.getLogger(TaskListService.class);

    public TaskListService(TaskListRepository taskListRepository) {
        this.taskListRepository = taskListRepository;
    }

    public TaskList findById(Long id) throws NoSuchElementException{
        return taskListRepository.findById(id).orElse(null);
    }

    public List<TaskList> findByUser(User user){
        if(taskListRepository.existsByOwner(user)){
            return taskListRepository.findByOwnerAndVisible(user, true).orElse(null);
        } else {
            return null;
        }
    }

    public Boolean existsById(Long id){
        return taskListRepository.existsById(id);
    }

    public void save(TaskList taskList) {
        taskListRepository.save(taskList);
    }

    public Boolean remove(TaskList taskList) {
        if(taskListRepository.existsById(taskList.getId())){
            if(softDelete){
                taskList.setVisible(false);
                taskListRepository.save(taskList);
                return !taskList.getVisible();
            } else {
                taskListRepository.delete(taskList);
                return true;
            }
        } else {
            return false;
        }
    }

    public Boolean removeById(Long id) {
        Optional<TaskList> taskList = taskListRepository.findById(id);
        if (taskList.isPresent()) {
            if (taskList.get().getVisible()) {
                return remove(taskList.get());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
