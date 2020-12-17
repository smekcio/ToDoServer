package pl.wsb.students.todolist.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.wsb.students.todolist.models.Task;
import pl.wsb.students.todolist.models.TaskList;
import pl.wsb.students.todolist.repository.TaskRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;

    @Value("${todo.app.softdelete}")
    private Boolean softDelete;

    private static final Logger logger = LoggerFactory.getLogger(TaskListService.class);

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasks(Long taskListId){
        return taskRepository.findByTaskListIdAndVisible(taskListId, true).orElse(new ArrayList<>());
    }

    public void save(Task task){
        taskRepository.save(task);
        task.getTaskList().setModified();
    }

    public Boolean remove(Task task) {
        if(taskRepository.existsById(task.getId())){
            if(softDelete){
                task.setVisible(false);
                taskRepository.save(task);
                task.getTaskList().setModified();
                return !task.getVisible();
            } else {
                taskRepository.delete(task);
                return true;
            }
        } else {
            return false;
        }
    }

    public Boolean removeById(Long id){
        Optional<Task> task = taskRepository.findById(id);
        if(task.isPresent()){
            if(task.get().getVisible()){
                return remove(task.get());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Task findById(Long id){
        Optional<Task> task = taskRepository.findById(id);
        return task.isPresent() ? task.get() : null;
    }

    public Boolean existsById(Long id){
        if(taskRepository.existsById(id)){
            return taskRepository.findById(id).get().getVisible();
        } else {
            return false;
        }
    }

    public Long countByTaskListId(Long taskListId){
        return taskRepository.countByTaskListIdAndVisible(taskListId, true).get();
    }

    public Long countByTaskList(TaskList taskList){
        return countByTaskListId(taskList.getId());
    }
}
