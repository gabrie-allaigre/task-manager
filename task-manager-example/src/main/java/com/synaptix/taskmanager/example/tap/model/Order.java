package com.synaptix.taskmanager.example.tap.model;

import com.synaptix.taskmanager.jpa.model.IBusinessTaskObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity(name = "T_ORDER")
public class Order implements IBusinessTaskObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private int version;

    @Column(name = "ORDER_STATUS")
    private String status;

    // TaskManager
    @Column(name = "CLUSTER_ID")
    private Long clusterId;

    public final Long getId() {
        return id;
    }

    @Override
    public Long getClusterId() {
        return clusterId;
    }

    @Override
    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return id != null && ((Order) obj).id != null ? id.equals(((Order) obj).id) : super.equals(obj);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("version", version).append("clusterId", clusterId).append("status", status).build();
    }
}
