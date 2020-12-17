package pl.wsb.students.todolist.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "taskList")
public class TaskList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User owner;

    @NotBlank
    private String name;

    @NotNull
    @Column(columnDefinition = "boolean default true")
    private Boolean visible;

    @NotNull
    private Date created;

    @NotNull
    private Date modified;

    public TaskList(){}

    public TaskList(String name, User owner){
        this.name = name;
        this.owner = owner;
        this.visible = true;
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
        this.created = this.modified = Date.from(zonedDateTime.toInstant());
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
        setModified();
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
        setModified();
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
        setModified();
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
        this.modified = Date.from(zonedDateTime.toInstant());
    }
}
