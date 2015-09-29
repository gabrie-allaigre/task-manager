package com.synaptix.taskmanager.example.jpa.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	private String type;
	private String status;
	@ManyToOne
	private Cluster cluster;
	@ManyToOne
	private Todo todo;

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
}
