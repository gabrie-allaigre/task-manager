package com.synaptix.taskmanager.example.tap.model;

import com.synaptix.taskmanager.jpa.model.IEntity;
import com.synaptix.taskmanager.jpa.model.Task;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
public class Item implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private int version;

    private String type;

    private boolean done;

    @Column(name = "FICHE_CONTACT_STATUS_DONE")
    @Enumerated(EnumType.STRING)
    private FicheContactStatus ficheContactStatusDone;

    @ManyToOne
    private FicheContact ficheContact;

    @ManyToOne
    private Task task;

    @Override
    public Long getId() {
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public FicheContactStatus getFicheContactStatusDone() {
        return ficheContactStatusDone;
    }

    public void setFicheContactStatusDone(FicheContactStatus ficheContactStatusDone) {
        this.ficheContactStatusDone = ficheContactStatusDone;
    }

    public FicheContact getFicheContact() {
        return ficheContact;
    }

    public void setFicheContact(FicheContact ficheContact) {
        this.ficheContact = ficheContact;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return id != null && ((Item) obj).id != null ? id.equals(((Item) obj).id) : super.equals(obj);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("version", version).append("type", type).append("done", done).append("ficheContactStatusDone", ficheContactStatusDone).build();
    }
}
