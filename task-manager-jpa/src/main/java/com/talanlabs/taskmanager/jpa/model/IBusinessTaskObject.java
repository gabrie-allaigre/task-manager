package com.talanlabs.taskmanager.jpa.model;

import com.talanlabs.taskmanager.model.ITaskObject;

public interface IBusinessTaskObject extends IEntity, ITaskObject {

    Long getClusterId();

    void setClusterId(Long clusterId);

}
