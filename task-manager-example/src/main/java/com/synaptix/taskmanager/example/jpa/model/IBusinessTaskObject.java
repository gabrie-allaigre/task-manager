package com.synaptix.taskmanager.example.jpa.model;

import com.synaptix.taskmanager.model.ITaskObject;

public interface IBusinessTaskObject extends IEntity,ITaskObject {

	Long getClusterId();

	void setClusterId(Long clusterId);

}
