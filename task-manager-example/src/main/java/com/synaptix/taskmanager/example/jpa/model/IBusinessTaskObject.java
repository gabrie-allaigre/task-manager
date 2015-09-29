package com.synaptix.taskmanager.example.jpa.model;

import com.synaptix.taskmanager.model.ITaskObject;

public interface IBusinessTaskObject extends IEntity,ITaskObject {

	Cluster getCluster();

	void setCluster(Cluster cluster);

}
