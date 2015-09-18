package com.talanlabs.taskmanager.example.jpa.model;

import com.talanlabs.taskmanager.jpa.model.IBusinessTaskObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
public class Todo implements IBusinessTaskObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private int version;
    private String summary;
    private String description;
    private String status;
    private String name;

    // TaskManager
    @Column(name = "CLUSTER_ID")
    private Long clusterId;

    @Override
    public final Long getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getClusterId() {
        return clusterId;
    }

    @Override
    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return id != null && ((Todo) obj).id != null ? id.equals(((Todo) obj).id) : super.equals(obj);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("version", version).append("summary", summary).append("description", description).append("status", status).append("name", name)
                .append("clusterId", clusterId).build();
    }
}
