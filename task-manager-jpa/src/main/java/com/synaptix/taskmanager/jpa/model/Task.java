package com.synaptix.taskmanager.jpa.model;

import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.jpa.converter.ClassConverter;
import com.synaptix.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
public class Task implements IStatusTask, ISubTask {

	public enum Type {
		statusTask, subTask
	}

	public enum Status {
		TODO, CURRENT, DONE, CANCEL
	}

	private transient final CurrentStatusTransform currentStatusTransform;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	@Column(name = "TYPE")
	@Enumerated(value = EnumType.STRING)
	private Type type;
	@Column(name = "STATUS")
	@Enumerated(value = EnumType.STRING)
	private Status status;
	@Column(name = "TASK_DEFINITION_CODE")
	private String codeTaskDefinition;
	@ManyToOne
	private Cluster cluster;
	@Column(name = "BUSINESS_TASK_OBJECT_ID")
	private Long businessTaskObjectId;
	@Column(name = "BUSINESS_TASK_OBJECT_CLASS", length = 512)
	@Convert(converter = ClassConverter.class)
	private Class<? extends IBusinessTaskObject> businessTaskObjectClass;
	@ManyToMany
	@JoinTable(
			name = "TASK_NEXT_TASK",
			joinColumns = { @JoinColumn(name = "TASK_ID", referencedColumnName = "ID") },
			inverseJoinColumns = { @JoinColumn(name = "NEXT_TASK_ID", referencedColumnName = "ID") })
	private List<Task> nextTasks;
	@ManyToMany(mappedBy = "nextTasks")
	private List<Task> previousTasks;

	//StatusTask
	@Column(name = "CURRENT_STATUS")
	private String currentStatus;
	@ManyToMany
	@JoinTable(
			name = "TASK_OTHER_BRANCH_TASK",
			joinColumns = { @JoinColumn(name = "TASK_ID", referencedColumnName = "ID") },
			inverseJoinColumns = { @JoinColumn(name = "OTHER_TASK_ID", referencedColumnName = "ID") })
	private List<Task> otherBranchFirstTasks;
	@ManyToMany(mappedBy = "otherBranchFirstTasks")
	private List<Task> parentOtherBranchFirstTasks;

	public Task() {
		this(MyCurrentStatusTransform.INSTANCE);
	}

	public Task(CurrentStatusTransform currentStatusTransform) {
		super();

		this.currentStatusTransform = currentStatusTransform;
	}

	public final Long getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
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

	public Long getBusinessTaskObjectId() {
		return businessTaskObjectId;
	}

	public void setBusinessTaskObjectId(Long businessTaskObjectId) {
		this.businessTaskObjectId = businessTaskObjectId;
	}

	public List<Task> getNextTasks() {
		return nextTasks;
	}

	public void setNextTasks(List<Task> nextTasks) {
		this.nextTasks = nextTasks;
	}

	public List<Task> getPreviousTasks() {
		return previousTasks;
	}

	public void setPreviousTasks(List<Task> previousTasks) {
		this.previousTasks = previousTasks;
	}

	@Override
	public Class<? extends ITaskObject> getTaskObjectClass() {
		return businessTaskObjectClass;
	}

	public Class<? extends IBusinessTaskObject> getBusinessTaskObjectClass() {
		return businessTaskObjectClass;
	}

	public void setBusinessTaskObjectClass(Class<? extends IBusinessTaskObject> businessTaskObjectClass) {
		this.businessTaskObjectClass = businessTaskObjectClass;
	}

	// StatusTask

	@Override
	public Object getCurrentStatus() {
		return currentStatusTransform.toObject(currentStatus);
	}

	public void setCurrentStatus(Object currentStatus) {
		this.currentStatus = currentStatusTransform.toString(currentStatus);
	}

	public List<Task> getOtherBranchFirstTasks() {
		return otherBranchFirstTasks;
	}

	public void setOtherBranchFirstTasks(List<Task> otherBranchFirstTasks) {
		this.otherBranchFirstTasks = otherBranchFirstTasks;
	}

	public List<Task> getParentOtherBranchFirstTasks() {
		return parentOtherBranchFirstTasks;
	}

	public void setParentOtherBranchFirstTasks(List<Task> parentOtherBranchFirstTasks) {
		this.parentOtherBranchFirstTasks = parentOtherBranchFirstTasks;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		return id != null && ((Task) obj).id != null ? id.equals(((Task) obj).id) : super.equals(obj);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("version", version).append("type", type).append("status", status).append("codeTaskDefinition", codeTaskDefinition)
				.append("businessTaskObjectClass", businessTaskObjectClass).append("currentStatus", currentStatus).append("cluster", cluster != null ? cluster.getId() : null)
				.append("businessTaskObjectId", businessTaskObjectId).append("nextTasks", nextTasks != null ? nextTasks.size() : 0)
				.append("previousTasks", previousTasks != null ? previousTasks.size() : 0).append("otherBranchFirstTasks", otherBranchFirstTasks != null ? otherBranchFirstTasks.size() : 0).build();
	}

	interface CurrentStatusTransform<E> {

		String toString(E currentStatus);

		E toObject(String currentStatusString);

	}

	private static class MyCurrentStatusTransform implements CurrentStatusTransform<String> {

		public static final CurrentStatusTransform<String> INSTANCE = new MyCurrentStatusTransform();

		@Override
		public String toString(String currentStatus) {
			return currentStatus;
		}

		@Override
		public String toObject(String currentStatusString) {
			return currentStatusString;
		}
	}
}
