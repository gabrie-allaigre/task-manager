package com.synaptix.taskmanager.example.jpa.model;

import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
public class Task implements IStatusTask, ISubTask {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	private String type;
	private String status;
	private String codeTaskDefinition;
	@ManyToOne
	private Cluster cluster;
	@ManyToOne
	private Todo todo;
	@OneToMany
	private List<Task> nextTasks;

	//StatusTask
	private Class<? extends ITaskObject> taskObjectClass;
	private String currentStatus;
	@OneToMany
	@JoinColumn(name = "otherBranchFirstTasks")
	private List<Task> otherBranchFirstTasks;

	public final Long getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getCodeTaskDefinition() {
		return codeTaskDefinition;
	}

	public void setCodeTaskDefinition(String codeTaskDefinition) {
		this.codeTaskDefinition = codeTaskDefinition;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public Todo getTodo() {
		return todo;
	}

	public void setTodo(Todo todo) {
		this.todo = todo;
	}

	public List<Task> getNextTasks() {
		return nextTasks;
	}

	public void setNextTasks(List<Task> nextTasks) {
		this.nextTasks = nextTasks;
	}

	// StatusTask

	@Override
	public Class<? extends ITaskObject> getTaskObjectClass() {
		return taskObjectClass;
	}

	public void setTaskObjectClass(Class<? extends ITaskObject> taskObjectClass) {
		this.taskObjectClass = taskObjectClass;
	}

	@Override
	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public List<Task> getOtherBranchFirstTasks() {
		return otherBranchFirstTasks;
	}

	public void setOtherBranchFirstTasks(List<Task> otherBranchFirstTasks) {
		this.otherBranchFirstTasks = otherBranchFirstTasks;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return id != null && ((Task) obj).id != null ? id.equals(((Task) obj).id) : super.equals(obj);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("version", version).append("type", type).append("status", status).append("codeTaskDefinition", codeTaskDefinition)
				.append("taskObjectClass", taskObjectClass).append("currentStatus", currentStatus).append("cluster", cluster != null ? cluster.getId() : null)
				.append("todo", todo != null ? todo.getId() : null).append("nextTasks", nextTasks != null ? nextTasks.size() : 0)
				.append("otherBranchFirstTasks", otherBranchFirstTasks != null ? otherBranchFirstTasks.size() : 0).build();
	}
}
