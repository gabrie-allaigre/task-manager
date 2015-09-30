package com.synaptix.taskmanager.jpa.model;

import com.synaptix.taskmanager.model.ITaskCluster;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
public class Cluster implements IEntity,ITaskCluster {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	@Column(name = "CHECK_GRAPH_CREATED")
	private boolean checkGraphCreated;
	@Column(name = "CHECK_ARCHIVED")
	private boolean checkArchived;
	@OneToMany
	@JoinColumn(name = "CLUSTER_ID")
	private List<ClusterDependency> clusterDependencies;

	@Override
	public final Long getId() {
		return id;
	}

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

	public List<ClusterDependency> getClusterDependencies() {
		return clusterDependencies;
	}

	public void setClusterDependencies(List<ClusterDependency> clusterDependencies) {
		this.clusterDependencies = clusterDependencies;
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
		return new ToStringBuilder(this).append("id", id).append("version", version).append("checkGraphCreated", checkGraphCreated).append("checkArchived",checkArchived).append("clusterDependencies",
				clusterDependencies != null ? clusterDependencies.size() : 0).build();
	}
}
