package com.synaptix.taskmanager.example.jpa.model;

import com.synaptix.taskmanager.model.ITaskCluster;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
public class Cluster implements ITaskCluster {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	private boolean checkGraphCreated;
	private boolean checkArchived;
	@OneToMany(mappedBy = "cluster")
	private List<Todo> todos;

	@Override
	public boolean isCheckGraphCreated() {
		return checkGraphCreated;
	}

	public void setCheckGraphCreated(boolean checkGraphCreated) {
		this.checkGraphCreated = checkGraphCreated;
	}

	@Override
	public boolean isCheckArchived() {
		return checkArchived;
	}

	public void setCheckArchived(boolean checkArchived) {
		this.checkArchived = checkArchived;
	}

	public List<Todo> getTodos() {
		return todos;
	}

	public void setTodos(List<Todo> todos) {
		this.todos = todos;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return id != null && ((Cluster)obj).id != null ? id.equals(((Cluster)obj).id) : super.equals(obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
