package pl.wsb.students.todolist.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class UserTaskListResponse {
    @JsonProperty("id")
    private Long taskListId;
    @JsonProperty("name")
    private String taskListName;
    private Integer numTasks;
    private Date created;
    private Date modified;
    private Boolean done;

    public UserTaskListResponse(Long taskListId, String taskListName, Integer numTasks, Boolean done, Date created, Date modified){
        this.taskListId = taskListId;
        this.taskListName = taskListName;
        this.numTasks = numTasks;
        this.created = created;
        this.modified = modified;
        this.done = done;
    }

    public Long getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(Long taskListId) {
        this.taskListId = taskListId;
    }

    public String getTaskListName() {
        return taskListName;
    }

    public void setTaskListName(String taskListName) {
        this.taskListName = taskListName;
    }

    public Integer getNumTasks() {
        return numTasks;
    }

    public void setNumTasks(Integer numTasks) {
        this.numTasks = numTasks;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
}
