package com.synaptix.taskmanager.example.tap.model;

import com.synaptix.taskmanager.jpa.model.IEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity(name = "T_ITEM")
public class Item implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private int version;

    private String type;

    private boolean done;

    @Column(name = "ORDER_STATUS_DONE")
    private String orderStatusDone;

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

    public String getOrderStatusDone() {
        return orderStatusDone;
    }

    public void setOrderStatusDone(String orderStatusDone) {
        this.orderStatusDone = orderStatusDone;
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
        return new ToStringBuilder(this).append("id", id).append("version", version).append("type", type).append("done", done).append("orderStatusDone", orderStatusDone).build();
    }
}
