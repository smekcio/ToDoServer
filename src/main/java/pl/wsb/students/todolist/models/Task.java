package pl.wsb.students.todolist.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private TaskList taskList;

    @NotNull
    @Column(columnDefinition = "boolean default false")
    private Boolean done;

    @NotBlank
    @Size(max = 200)
    private String payload;

    @NotNull
    @Column(columnDefinition = "boolean default true")
    private Boolean visible;

    public Task(){};

    public Task(TaskList taskList, String payload){
        this.taskList = taskList;
        this.payload = payload;
        this.done = false;
        this.visible = true;
    }

    public Task(Long id, TaskList taskList, String payload){
        this.id = id;
        this.taskList = taskList;
        this.payload = payload;
        this.done = false;
        this.visible = true;
    }

    public Task(Long id, TaskList taskList, String payload, Boolean done){
        this.id = id;
        this.taskList = taskList;
        this.payload = payload;
        this.done = done;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
