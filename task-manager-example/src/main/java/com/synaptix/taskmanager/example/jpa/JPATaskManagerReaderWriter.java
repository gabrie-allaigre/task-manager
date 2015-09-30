package com.synaptix.taskmanager.example.jpa;

import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.example.jpa.model.Cluster;
import com.synaptix.taskmanager.example.jpa.model.ClusterDependency;
import com.synaptix.taskmanager.example.jpa.model.IBusinessTaskObject;
import com.synaptix.taskmanager.example.jpa.model.Task;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JPATaskManagerReaderWriter implements ITaskManagerReader, ITaskManagerWriter {

	private static final Log LOG = LogFactory.getLog(JPATaskManagerReaderWriter.class);

	private final IJPAAccess jpaAccess;

	public JPATaskManagerReaderWriter(IJPAAccess jpaAccess) {
		super();

		this.jpaAccess = jpaAccess;
	}

	public IJPAAccess getJpaAccess() {
		return jpaAccess;
	}

	// WRITER

	@Override
	public ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster) {
		LOG.info("JPARW - saveNewTaskCluster");

		Cluster cluster = (Cluster) taskCluster;

		getJpaAccess().getEntityManager().persist(cluster);

		return cluster;
	}

	@Override
	public ITaskCluster saveNewGraphFromTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject, IStatusTask>> taskObjectTasks) {
		LOG.info("JPARW - saveNewTaskClusterForTaskObject");

		getJpaAccess().getEntityManager().getTransaction().begin();

		Cluster cluster = (Cluster) taskCluster;
		List<ClusterDependency> cds = cluster.getClusterDependencies();
		if (cds == null) {
			cds = new ArrayList<ClusterDependency>();
			cluster.setClusterDependencies(cds);
		}

		if (taskObjectTasks != null && !taskObjectTasks.isEmpty()) {
			for (Pair<ITaskObject, IStatusTask> taskObjectNode : taskObjectTasks) {
				IBusinessTaskObject bto = (IBusinessTaskObject) taskObjectNode.getLeft();
				bto.setClusterId(cluster.getId());

				Task statusTask = (Task) taskObjectNode.getRight();
				statusTask.setStatus(Task.Status.CURRENT);
				statusTask.setCluster(cluster);
				statusTask.setBusinessTaskObjectId(bto.getId());
				getJpaAccess().getEntityManager().persist(statusTask);

				ClusterDependency cd = new ClusterDependency();
				cd.setBusinessTaskObjectClass(bto.getClass());
				cd.setBusinessTaskObjectId(bto.getId());
				getJpaAccess().getEntityManager().persist(cd);

				cds.add(cd);
			}
		}

		cluster.setCheckGraphCreated(true);
		getJpaAccess().getEntityManager().persist(cluster);

		getJpaAccess().getEntityManager().getTransaction().commit();

		return cluster;
	}

	@Override
	public void saveRemoveTaskObjectsFromTaskCluster(ITaskCluster taskCluster, List<ITaskObject> taskObjects) {
		LOG.info("JPARW - saveRemoveTaskObjectsFromTaskCluster");

		Cluster cluster = (Cluster) taskCluster;
		List<ClusterDependency> cds = cluster.getClusterDependencies();


	}

	@Override
	public ITaskCluster saveMoveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, Map<ITaskCluster, List<ITaskObject>> modifyClusterMap, boolean newTaskCluster) {
		LOG.info("JPARW - saveMoveTaskObjectsToTaskCluster");
		return null;
	}

	@Override
	public ITaskCluster archiveTaskCluster(ITaskCluster taskCluster) {
		LOG.info("JPARW - archiveTaskCluster");

		Cluster cluster = (Cluster) taskCluster;
		cluster.setCheckArchived(true);
		getJpaAccess().getEntityManager().persist(cluster);

		return cluster;
	}

	@Override
	public void saveNextTasksInTaskCluster(ITaskCluster taskCluster, ICommonTask toDoneTask, Object taskServiceResult, List<ICommonTask> nextCurrentTasks) {
		LOG.info("JPARW - saveNextTasksInTaskCluster");

		getJpaAccess().getEntityManager().getTransaction().begin();

		Cluster cluster = (Cluster) taskCluster;

		Task tdt = (Task) toDoneTask;
		tdt.setStatus(Task.Status.DONE);
		getJpaAccess().getEntityManager().persist(tdt);

		if (nextCurrentTasks != null && !nextCurrentTasks.isEmpty()) {
			for (ICommonTask nextCurrentTask : nextCurrentTasks) {
				Task nct = (Task) nextCurrentTask;
				nct.setStatus(Task.Status.CURRENT);
				getJpaAccess().getEntityManager().persist(nct);
			}
		}

		getJpaAccess().getEntityManager().getTransaction().commit();
	}

	@Override
	public void saveNothingTask(ITaskCluster taskCluster, ICommonTask nothingTask, Object taskServiceResult, Throwable errorMessage) {
		LOG.info("JPARW - saveNothingTask");
	}

	@Override
	public void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, IStatusTask toDoneTask, Object taskServiceResult, List<ICommonTask> newTasks, Map<ISubTask, List<ICommonTask>> linkNextTasksMap,
			Map<IStatusTask, List<ICommonTask>> otherBranchFirstTasksMap, List<ICommonTask> nextCurrentTasks, List<ICommonTask> deleteTasks) {
		LOG.info("JPARW - saveNewNextTasksInTaskCluster");

		getJpaAccess().getEntityManager().getTransaction().begin();

		Cluster cluster = (Cluster) taskCluster;
		Task tdt = (Task) toDoneTask;
		tdt.setStatus(Task.Status.DONE);
		getJpaAccess().getEntityManager().persist(tdt);

		if (newTasks != null && !newTasks.isEmpty()) {
			for (ICommonTask newTask : newTasks) {
				Task nct = (Task) newTask;
				nct.setStatus(Task.Status.TODO);
				nct.setCluster(cluster);
				nct.setBusinessTaskObjectClass(tdt.getBusinessTaskObjectClass());
				nct.setBusinessTaskObjectId(tdt.getBusinessTaskObjectId());

				getJpaAccess().getEntityManager().persist(nct);
			}
		}

		if (linkNextTasksMap != null && !linkNextTasksMap.isEmpty()) {
			for (Map.Entry<ISubTask, List<ICommonTask>> entry : linkNextTasksMap.entrySet()) {
				Task nct = (Task) entry.getKey();

				List<Task> childs = new ArrayList<Task>();
				List<ICommonTask> ts = entry.getValue();
				if (ts != null && !ts.isEmpty()) {
					for (ICommonTask t : ts) {
						childs.add((Task) t);
					}
				}
				nct.setNextTasks(childs);

				getJpaAccess().getEntityManager().persist(nct);
			}
		}

		if (otherBranchFirstTasksMap != null && !otherBranchFirstTasksMap.isEmpty()) {
			for (Map.Entry<IStatusTask, List<ICommonTask>> entry : otherBranchFirstTasksMap.entrySet()) {
				Task nct = (Task) entry.getKey();

				List<Task> childs = new ArrayList<Task>();
				List<ICommonTask> ts = entry.getValue();
				if (ts != null && !ts.isEmpty()) {
					for (ICommonTask t : ts) {
						childs.add((Task) t);
					}
				}
				nct.setOtherBranchFirstTasks(childs);

				getJpaAccess().getEntityManager().persist(nct);
			}
		}

		if (nextCurrentTasks != null && !nextCurrentTasks.isEmpty()) {
			List<Task> childs = new ArrayList<Task>();
			for (ICommonTask nextCurrentTask : nextCurrentTasks) {
				Task nct = (Task) nextCurrentTask;
				nct.setStatus(Task.Status.CURRENT);

				getJpaAccess().getEntityManager().persist(nct);

				childs.add(nct);
			}

			tdt.setNextTasks(childs);
			getJpaAccess().getEntityManager().persist(tdt);
		}

		if (deleteTasks != null && !deleteTasks.isEmpty()) {
			for (ICommonTask deleteTask : deleteTasks) {
				Task nct = (Task) deleteTask;
				nct.setStatus(Task.Status.DELETE);

				getJpaAccess().getEntityManager().persist(nct);
			}
		}

		getJpaAccess().getEntityManager().getTransaction().commit();
	}

	// READER

	@Override
	public ITaskCluster findTaskClusterByTaskObject(ITaskObject taskObject) {
		LOG.info("JPARW - findTaskClusterByTaskObject");
		Long clusterId = ((IBusinessTaskObject) taskObject).getClusterId();
		return clusterId != null ? getJpaAccess().getEntityManager().find(Cluster.class, clusterId) : null;
	}

	@Override
	public List<? extends ITaskObject> findTaskObjectsByTaskCluster(ITaskCluster taskCluster) {
		LOG.info("JPARW - findTaskObjectsByTaskCluster");

		List<ITaskObject> res = new ArrayList<ITaskObject>();

		Cluster cluster = (Cluster) taskCluster;
		List<ClusterDependency> cds = cluster.getClusterDependencies();
		if (cds != null && !cds.isEmpty()) {
			for (ClusterDependency cd : cds) {
				getJpaAccess().find(cd.getBusinessTaskObjectClass(), cd.getBusinessTaskObjectId());
			}
		}

		return res;

	}

	@Override
	public List<? extends ICommonTask> findCurrentTasksByTaskCluster(ITaskCluster taskCluster) {
		LOG.info("JPARW - findCurrentTasksByTaskCluster");

		Cluster cluster = (Cluster) taskCluster;

		CriteriaBuilder cb = getJpaAccess().getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> root = cq.from(Task.class);
		cq.where(cb.and(cb.equal(root.get("status"), Task.Status.CURRENT), cb.equal(root.get("cluster").get("id"), cluster.getId())));
		TypedQuery<Task> q = getJpaAccess().getEntityManager().createQuery(cq);
		return q.getResultList();
	}

	@Override
	public List<? extends ICommonTask> findNextTasksBySubTask(ISubTask subTask) {
		LOG.info("JPARW - findNextTasksBySubTask");
		return ((Task) subTask).getNextTasks();
	}

	@Override
	public List<? extends ICommonTask> findOtherBranchFirstTasksByStatusTask(IStatusTask statusTask) {
		return ((Task) statusTask).getOtherBranchFirstTasks();
	}
}
