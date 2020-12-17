package pl.wsb.students.todolist.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import pl.wsb.students.todolist.models.Task;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponse {

    private Long id;
    private Boolean done;
    private String payload;
    private Long taskListId;

    public TaskResponse(Long id, Boolean done, String payload){
        this.id = id;
        this.done = done;
        this.payload = payload;
    }

    public TaskResponse(Long id, Boolean done, String payload, Long taskListId){
        this.id = id;
        this.done = done;
        this.payload = payload;
        this.taskListId = taskListId;
    }

    public static List<TaskResponse> generate(List<Task> tasks){
        List<TaskResponse> taskResponses = new ArrayList<TaskResponse>();
        if(tasks != null){
            for (Task task : tasks){
                taskResponses.add(new TaskResponse(task.getId(), task.getDone(), task.getPayload()));
            }

        }
        return taskResponses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Long getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(Long taskListId) {
        this.taskListId = taskListId;
    }
}