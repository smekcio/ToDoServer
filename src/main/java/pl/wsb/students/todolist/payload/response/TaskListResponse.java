package pl.wsb.students.todolist.payload.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;
import java.util.List;

@JsonPropertyOrder({
        "id",
        "name",
        "owner",
        "tasks",
        "created",
        "modified"
})
public class TaskListResponse {

    private Long id;
    private Long owner;
    private List<TaskResponse> tasks;
    private String name;
    private Date created;
    private Date modified;

    public TaskListResponse(Long id, Long owner, String name, List<TaskResponse> tasks, Date created, Date modified){
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.tasks = tasks;
        this.created = created;
        this.modified = modified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public List<TaskResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskResponse> tasks) {
        this.tasks = tasks;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}