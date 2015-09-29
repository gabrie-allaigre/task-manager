package com.synaptix.taskmanager.example.jpa.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
public class ClusterDependency implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;
	@ManyToOne
	private Cluster cluster;
	private Long businessTaskObjectId;
	private Class<? extends IBusinessTaskObject> businessTaskObjectClass;

	@Override
	public Long getId() {
		return id;
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

	public Class<? extends IBusinessTaskObject> getBusinessTaskObjectClass() {
		return businessTaskObjectClass;
	}

	public void setBusinessTaskObjectClass(Class<? extends IBusinessTaskObject> businessTaskObjectClass) {
		this.businessTaskObjectClass = businessTaskObjectClass;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return id != null && ((ClusterDependency) obj).id != null ? id.equals(((ClusterDependency) obj).id) : super.equals(obj);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("version", version).append("businessTaskObjectClass", businessTaskObjectClass).append("cluster", cluster != null ? cluster.getId() : null)
				.append("businessTaskObjectId", businessTaskObjectId).build();
	}
}
