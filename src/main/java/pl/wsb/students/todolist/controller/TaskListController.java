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
import pl.wsb.students.todolist.payload.request.TaskListRequest;
import pl.wsb.students.todolist.payload.response.MessageResponse;
import pl.wsb.students.todolist.payload.response.TaskListResponse;
import pl.wsb.students.todolist.payload.response.TaskResponse;
import pl.wsb.students.todolist.payload.response.UserTaskListResponse;
import pl.wsb.students.todolist.service.TaskListService;
import pl.wsb.students.todolist.service.TaskService;
import pl.wsb.students.todolist.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasklist")
public class TaskListController {
    @Autowired
    private TaskListService taskListService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @GetMapping("/")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getTaskLists(){
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<TaskList> taskLists = taskListService.findByUser(userService.findByUsername(userDetails.getUsername()));
        if(taskLists != null){
            List<UserTaskListResponse> response = new ArrayList<>();
            for(TaskList tl : taskLists){
                List<Task> tasks = taskService.getTasks(tl.getId());
                Integer numTasks = tasks.size();
                Boolean listDone = true;
                if(numTasks>0){
                    for(Task t : tasks){
                        if(!t.getDone()){
                            listDone = false;
                            break;
                        }
                    }
                } else {
                    listDone = false;
                }
                response.add(new UserTaskListResponse(tl.getId(), tl.getName(), numTasks, listDone, tl.getCreated(), tl.getModified()));
            }
            logger.info("{} get all lists", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            logger.info("User has no to-do lists!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getTaskList(@PathVariable("id") Long id){
        TaskList taskList = taskListService.findById(id);
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Czy lista istnieje
        if(taskList==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Error: To-do list not found!"));
        }

        // Czy użytkownik jest właścicielem listy
        if(taskList.getOwner() != userService.findByUsername(userDetails.getUsername())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Error: You are not the owner of the to-do list!"));
        }

        List<TaskResponse> taskResponses = TaskResponse.generate(taskService.getTasks(taskList.getId()));

        return ResponseEntity.ok(new TaskListResponse(taskList.getId(), taskList.getOwner().getId(), taskList.getName() ,taskResponses, taskList.getCreated(), taskList.getModified()));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> createTaskList(@Valid @RequestBody TaskListRequest taskListRequest){
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try{
            TaskList taskList = new TaskList(taskListRequest.getName(), userService.findByUsername(userDetails.getUsername()));
            taskListService.save(taskList);
            return ResponseEntity.status(HttpStatus.OK).body(new TaskListResponse(taskList.getId(), taskList.getOwner().getId(), taskList.getName(), new ArrayList<TaskResponse>(), taskList.getCreated(), taskList.getModified()));
        } catch (Exception e){
            logger.error("Task list did not created: {}", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping()
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateTaskList(@Valid @RequestBody TaskListRequest taskListRequest){
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(taskListRequest.getId() == null){
            logger.info("Error: Missing ID field!");
            logger.info(taskListRequest.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        TaskList taskList = taskListService.findById(taskListRequest.getId());

        // Czy lista istnieje
        if(taskList==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Error: To-do list not found!"));
        }

        // Czy użytkownik jest właścicielem listy
        if(taskList.getOwner() != userService.findByUsername(userDetails.getUsername())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Error: You are not the owner of the to-do list!"));
        }

        taskList.setName(taskListRequest.getName());
        taskListService.save(taskList);

        List<TaskResponse> taskResponses = TaskResponse.generate(taskService.getTasks(taskList.getId()));
        return ResponseEntity.ok(new TaskListResponse(taskList.getId(), taskList.getOwner().getId(), taskList.getName() ,taskResponses, taskList.getCreated(), taskList.getModified()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteTaskList(@PathVariable("id") Long ListId){
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TaskList taskList = taskListService.findById(ListId);

        // Czy lista istnieje
        if(taskList==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Error: To-do list not found!"));
        }

        // Czy użytkownik jest właścicielem listy
        if(taskList.getOwner() != userService.findByUsername(userDetails.getUsername())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Error: You are not the owner of the to-do list!"));
        }

        // Usuwanie listy zadan
        if(taskListService.removeById(ListId)){
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            logger.info("Task list {} not found", ListId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
