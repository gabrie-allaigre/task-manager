package com.talanlabs.taskmanager.jpa.test.data;

import com.talanlabs.taskmanager.jpa.model.IBusinessTaskObject;

import javax.persistence.*;
import java.util.Date;

@Entity
public class BusinessObject implements IBusinessTaskObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    // TaskManager
    @Column(name = "CLUSTER_ID")
    private Long clusterId;

    private String status;

    private String code;

    private Date date;

    @Override
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
