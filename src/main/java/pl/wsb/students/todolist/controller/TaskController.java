package pl.wsb.students.todolist.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.wsb.students.todolist.models.Task;
import pl.wsb.students.todolist.models.TaskList;
import pl.wsb.students.todolist.payload.request.TaskRequest;
import pl.wsb.students.todolist.payload.response.MessageResponse;
import pl.wsb.students.todolist.payload.response.TaskResponse;
import pl.wsb.students.todolist.service.TaskListService;
import pl.wsb.students.todolist.service.TaskService;
import pl.wsb.students.todolist.service.UserService;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/task")
public class TaskController {
    @Autowired
    private TaskListService taskListService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> addNewTask(@Valid @RequestBody TaskRequest taskRequest){
        TaskList taskList = taskListService.findById(taskRequest.getTaskListId());

        // Czy lista istnieje
        if(!taskListService.existsById(taskRequest.getTaskListId())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Error: To-do list not found!"));
        }

        // Czy użytkownik jest właścicielem listy
        if(!isOwner(taskList.getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Error: You are not the owner of the to-do list!"));
        }

        Task task = new Task(taskList, taskRequest.getPayload());
        taskService.save(task);
        return ResponseEntity.ok(new TaskResponse(task.getId(), task.getDone(), task.getPayload(), taskRequest.getTaskListId()));
    }

    @PutMapping()
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> editTask(@Valid @RequestBody TaskRequest taskRequest){
        // Czy podano id zadania
        if(taskRequest.getId() == null){
            logger.error("Error: Task id cannot be null!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Pobieranie zadania
        Task task = taskService.findById(taskRequest.getId());

        // Czy rozni sie tresc
        if(!taskRequest.getPayload().equals(task.getPayload())){
            logger.error("Info: Payload is equal");
            return ResponseEntity.status(HttpStatus.OK).body(new TaskResponse(taskRequest.getId(), taskRequest.getDone(), taskRequest.getPayload(), taskRequest.getTaskListId()));
        }

        // Czy lista do tego zadania istnieje
        if(!taskListService.existsById(taskRequest.getTaskListId())){
            logger.error("Error: Task list id does not exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Czy zadanie istnieje
        if(!taskService.existsById(taskRequest.getId())){
            logger.error("Error: Task does not exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Pobieranie listy zadan i uzytkownika
        TaskList taskList = taskListService.findById(taskRequest.getTaskListId());

        // Czy użytkownik jest właścicielem listy
        if(!isOwner(taskList.getId())){
            logger.error("Error: You are not the owner of the to-do list! ({})" ,taskList.getOwner().getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(task.getVisible()){
            task.setDone(taskRequest.getDone());
            task.setPayload(taskRequest.getPayload());
            taskService.save(task);
            return ResponseEntity.status(HttpStatus.OK).body(new TaskResponse(task.getId(), task.getDone(), task.getPayload(), taskRequest.getTaskListId()));
        } else {
            logger.error("Error: Task does not exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleTask(@PathVariable("id") Long id){
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Czy zadanie istnieje
        if(!taskService.existsById(id)){
            logger.error("Error: Task does not exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Task task = taskService.findById(id);

        // Czy użytkownik jest właścicielem listy
        if(!task.getTaskList().getOwner().getUsername().equals(userDetails.getUsername())){
            logger.error("Error: You are not the owner of the to-do list! ({}) != ({})" ,task.getTaskList().getOwner().getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        task.setDone(!task.getDone());
        taskService.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(task.getDone().toString()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable("id") Long id){

        // Czy zadanie istnieje
        if(!taskService.existsById(id)){
            logger.error("Error: Task does not exist!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Pobieranie zadania
        Task task = taskService.findById(id);

        // Czy zadanie jest oznaczone jako usuniete
        if(!task.getVisible()){
            logger.error("Error: Task is soft deleted!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Czy lista do tego zadania istnieje
        if(!taskListService.existsById(task.getTaskList().getId())){
            logger.error("Error: Task list id does not exist!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Pobieranie listy zadan i uzytkownika
        TaskList taskList = task.getTaskList();

        // Czy użytkownik jest właścicielem listy
        if(!isOwner(taskList.getId())){
            logger.error("Error: You are not the owner of the to-do list! ({})" ,taskList.getOwner().getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Usuwanie zadania
        if(taskService.removeById(id)){
            logger.info("Task {} removed successfully", id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            logger.info("Error: Task {} has not been deleted!", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private Boolean isOwner(Long taskListId){
        TaskList taskList = taskListService.findById(taskListId);
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Czy użytkownik jest właścicielem listy
        return taskList.getOwner() == userService.findByUsername(userDetails.getUsername());
    }
}
