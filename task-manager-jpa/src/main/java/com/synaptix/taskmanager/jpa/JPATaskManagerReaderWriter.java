package com.synaptix.taskmanager.jpa;

import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.synaptix.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.task.IStatusTask;
import com.synaptix.taskmanager.engine.task.ISubTask;
import com.synaptix.taskmanager.jpa.model.Cluster;
import com.synaptix.taskmanager.jpa.model.ClusterDependency;
import com.synaptix.taskmanager.jpa.model.IBusinessTaskObject;
import com.synaptix.taskmanager.jpa.model.Task;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JPATaskManagerReaderWriter implements ITaskManagerReader, ITaskManagerWriter {

    private static final Log LOG = LogFactory.getLog(JPATaskManagerReaderWriter.class);

    private final IJPAAccess jpaAccess;

    private final RemoveMode removeMode;

    public JPATaskManagerReaderWriter(IJPAAccess jpaAccess) {
        this(jpaAccess, RemoveMode.CANCEL);
    }

    public JPATaskManagerReaderWriter(IJPAAccess jpaAccess, RemoveMode removeMode) {
        super();

        this.jpaAccess = jpaAccess;
        this.removeMode = removeMode;
    }

    public IJPAAccess getJpaAccess() {
        return jpaAccess;
    }

    @Override
    public ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster) {
        LOG.info("JPARW - saveNewTaskCluster");

        getJpaAccess().getEntityManager().getTransaction().begin();

        Cluster cluster = (Cluster) taskCluster;

        getJpaAccess().getEntityManager().persist(cluster);

        getJpaAccess().getEntityManager().getTransaction().commit();

        return cluster;
    }

    // WRITER

    @Override
    public ITaskCluster saveNewGraphFromTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject, IStatusTask>> taskObjectTasks) {
        LOG.info("JPARW - saveNewTaskClusterForTaskObject");

        getJpaAccess().getEntityManager().getTransaction().begin();

        Cluster cluster = (Cluster) taskCluster;
        List<ClusterDependency> cds = cluster.getClusterDependencies();
        if (cds == null) {
            cds = new ArrayList<>();
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

        getJpaAccess().getEntityManager().getTransaction().begin();

        Cluster cluster = (Cluster) taskCluster;
        List<ClusterDependency> cds = cluster.getClusterDependencies();

        if (cds != null && !cds.isEmpty() && taskObjects != null && !taskObjects.isEmpty()) {
            Iterator<ClusterDependency> cdIt = cds.iterator();
            while (cdIt.hasNext()) {
                ClusterDependency cd = cdIt.next();

                // Search task object for cluster dependency
                boolean find = false;
                Iterator<ITaskObject> it = taskObjects.iterator();
                while (it.hasNext() && !find) {
                    IBusinessTaskObject businessTaskObject = (IBusinessTaskObject) it.next();
                    Class<? extends IBusinessTaskObject> businessTaskObjectClass = getJpaAccess().instanceToClass(businessTaskObject);
                    if (businessTaskObjectClass.equals(cd.getBusinessTaskObjectClass()) && cd.getBusinessTaskObjectId().equals(businessTaskObject.getId())) {
                        // Remove Cluster Dependency
                        getJpaAccess().getEntityManager().remove(cd);
                        cdIt.remove();

                        // Remove cluster in task object
                        businessTaskObject.setClusterId(null);
                        getJpaAccess().getEntityManager().persist(businessTaskObject);

                        // Remove all task for task object and cluster
                        CriteriaBuilder cb = getJpaAccess().getEntityManager().getCriteriaBuilder();
                        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
                        Root<Task> root = cq.from(Task.class);
                        cq.where(cb.and(cb.equal(root.get("cluster").get("id"), cluster.getId()), cb.equal(root.get("businessTaskObjectId"), businessTaskObject.getId()),
                                cb.equal(root.get("businessTaskObjectClass"), businessTaskObjectClass)));
                        TypedQuery<Task> q = getJpaAccess().getEntityManager().createQuery(cq);
                        List<Task> tasks = q.getResultList();
                        if (tasks != null && !tasks.isEmpty()) {
                            for (Task task : tasks) {
                                getJpaAccess().getEntityManager().remove(task);
                            }
                        }

                        find = true;
                    }
                }
            }
        }

        getJpaAccess().getEntityManager().getTransaction().commit();
    }

    @Override
    public ITaskCluster saveMoveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, Map<ITaskCluster, List<ITaskObject>> modifyClusterMap) {
        LOG.info("JPARW - saveMoveTaskObjectsToTaskCluster");

        getJpaAccess().getEntityManager().getTransaction().begin();

        Cluster dstCluster = (Cluster) dstTaskCluster;

        if (modifyClusterMap != null && !modifyClusterMap.isEmpty()) {
            List<ClusterDependency> dstCds = dstCluster.getClusterDependencies();
            if (dstCds == null) {
                dstCds = new ArrayList<>();
                dstCluster.setClusterDependencies(dstCds);
            }

            for (Map.Entry<ITaskCluster, List<ITaskObject>> entry : modifyClusterMap.entrySet()) {
                Cluster srcCluster = (Cluster) entry.getKey();
                List<ClusterDependency> srcCds = srcCluster.getClusterDependencies();

                List<ITaskObject> taskObjects = entry.getValue();

                if (srcCds != null && !srcCds.isEmpty()) {
                    Iterator<ClusterDependency> cdIt = srcCds.iterator();
                    while (cdIt.hasNext()) {
                        ClusterDependency srcCd = cdIt.next();

                        // Search task object for cluster dependency
                        boolean find = false;
                        Iterator<ITaskObject> it = taskObjects.iterator();
                        while (it.hasNext() && !find) {
                            IBusinessTaskObject businessTaskObject = (IBusinessTaskObject) it.next();
                            Class<? extends IBusinessTaskObject> businessTaskObjectClass = getJpaAccess().instanceToClass(businessTaskObject);
                            if (businessTaskObjectClass.equals(srcCd.getBusinessTaskObjectClass()) && srcCd.getBusinessTaskObjectId().equals(businessTaskObject.getId())) {
                                // Remove cluster dependency for source cluster
                                getJpaAccess().getEntityManager().remove(srcCd);
                                cdIt.remove();

                                // Add cluster dependency in dest cluster
                                ClusterDependency dstCd = new ClusterDependency();
                                dstCd.setBusinessTaskObjectClass(srcCd.getBusinessTaskObjectClass());
                                dstCd.setBusinessTaskObjectId(srcCd.getBusinessTaskObjectId());
                                getJpaAccess().getEntityManager().persist(dstCd);
                                dstCds.add(dstCd);

                                // Change cluster in task object, source cluster to dest cluster
                                businessTaskObject.setClusterId(dstCluster.getId());
                                getJpaAccess().getEntityManager().persist(businessTaskObject);

                                // Move task, source cluster to dest cluster
                                CriteriaBuilder cb = getJpaAccess().getEntityManager().getCriteriaBuilder();
                                CriteriaQuery<Task> cq = cb.createQuery(Task.class);
                                Root<Task> root = cq.from(Task.class);
                                cq.where(cb.and(cb.equal(root.get("cluster").get("id"), srcCluster.getId()), cb.equal(root.get("businessTaskObjectId"), businessTaskObject.getId()),
                                        cb.equal(root.get("businessTaskObjectClass"), businessTaskObjectClass)));
                                TypedQuery<Task> q = getJpaAccess().getEntityManager().createQuery(cq);
                                List<Task> tasks = q.getResultList();
                                if (tasks != null && !tasks.isEmpty()) {
                                    for (Task task : tasks) {
                                        task.setCluster(dstCluster);
                                        getJpaAccess().getEntityManager().persist(task);
                                    }
                                }

                                find = true;
                            }
                        }
                    }

                    getJpaAccess().getEntityManager().persist(srcCluster);
                }
            }

            dstCluster.setCheckGraphCreated(true);
            getJpaAccess().getEntityManager().persist(dstCluster);
        }

        getJpaAccess().getEntityManager().getTransaction().commit();

        return dstCluster;
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

                List<Task> nextTasks = new ArrayList<>();
                List<ICommonTask> ts = entry.getValue();
                if (ts != null && !ts.isEmpty()) {
                    for (ICommonTask t : ts) {
                        Task nextTask = (Task) t;
                        nextTasks.add(nextTask);

                        List<Task> previousTasks = nextTask.getPreviousTasks();
                        if (previousTasks == null) {
                            previousTasks = new ArrayList<>();
                            nextTask.setPreviousTasks(previousTasks);
                        }
                        previousTasks.add(nct);

                        getJpaAccess().getEntityManager().persist(nextTask);
                    }
                }
                nct.setNextTasks(nextTasks);

                getJpaAccess().getEntityManager().persist(nct);
            }
        }

        if (otherBranchFirstTasksMap != null && !otherBranchFirstTasksMap.isEmpty()) {
            for (Map.Entry<IStatusTask, List<ICommonTask>> entry : otherBranchFirstTasksMap.entrySet()) {
                Task nct = (Task) entry.getKey();

                List<Task> childs = new ArrayList<>();
                List<ICommonTask> ts = entry.getValue();
                if (ts != null && !ts.isEmpty()) {
                    for (ICommonTask t : ts) {
                        Task otherTask = (Task) t;

                        childs.add(otherTask);

                        List<Task> parentTasks = otherTask.getParentOtherBranchFirstTasks();
                        if (parentTasks == null) {
                            parentTasks = new ArrayList<>();
                            otherTask.setParentOtherBranchFirstTasks(parentTasks);
                        }
                        parentTasks.add(tdt);

                        getJpaAccess().getEntityManager().persist(otherTask);
                    }
                }
                nct.setOtherBranchFirstTasks(childs);

                getJpaAccess().getEntityManager().persist(nct);
            }
        }

        if (nextCurrentTasks != null && !nextCurrentTasks.isEmpty()) {
            List<Task> childs = new ArrayList<>();
            for (ICommonTask nextCurrentTask : nextCurrentTasks) {
                Task nct = (Task) nextCurrentTask;
                nct.setStatus(Task.Status.CURRENT);

                childs.add(nct);

                List<Task> previousTasks = nct.getPreviousTasks();
                if (previousTasks == null) {
                    previousTasks = new ArrayList<>();
                    nct.setPreviousTasks(previousTasks);
                }
                previousTasks.add(tdt);

                getJpaAccess().getEntityManager().persist(nct);
            }

            tdt.setNextTasks(childs);
            getJpaAccess().getEntityManager().persist(tdt);
        }

        if (deleteTasks != null && !deleteTasks.isEmpty()) {
            for (ICommonTask deleteTask : deleteTasks) {
                Task nct = (Task) deleteTask;

                switch (removeMode) {
                case CANCEL:
                    nct.setStatus(Task.Status.CANCEL);
                    getJpaAccess().getEntityManager().persist(nct);
                    break;
                case DELETE:
                    getJpaAccess().getEntityManager().remove(nct);
                    break;
                }
            }
        }

        getJpaAccess().getEntityManager().getTransaction().commit();
    }

    @Override
    public ITaskCluster findTaskClusterByTaskObject(ITaskObject taskObject) {
        LOG.info("JPARW - findTaskClusterByTaskObject");
        Long clusterId = ((IBusinessTaskObject) taskObject).getClusterId();
        return clusterId != null ? getJpaAccess().getEntityManager().find(Cluster.class, clusterId) : null;
    }

    // READER

    @Override
    public List<? extends ITaskObject> findTaskObjectsByTaskCluster(ITaskCluster taskCluster) {
        LOG.info("JPARW - findTaskObjectsByTaskCluster");

        List<ITaskObject> res = new ArrayList<>();

        Cluster cluster = (Cluster) taskCluster;
        List<ClusterDependency> cds = cluster.getClusterDependencies();
        if (cds != null && !cds.isEmpty()) {
            for (ClusterDependency cd : cds) {
                res.add(getJpaAccess().find(cd.getBusinessTaskObjectClass(), cd.getBusinessTaskObjectId()));
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

        List<ICommonTask> res = new ArrayList<>();

        List<Task> nextTasks = ((Task) subTask).getNextTasks();
        if (nextTasks != null && !nextTasks.isEmpty()) {
            for (ICommonTask nextTask : nextTasks) {
                List<Task> previousTasks = ((Task) nextTask).getPreviousTasks();
                boolean allFinish = true;
                if (previousTasks != null && !previousTasks.isEmpty()) {
                    Iterator<Task> previousTaskIt = previousTasks.iterator();
                    while (previousTaskIt.hasNext() && allFinish) {
                        Task previousTask = previousTaskIt.next();
                        if (!previousTask.equals(subTask) && (Task.Status.TODO.equals(previousTask.getStatus()) || Task.Status.CURRENT.equals(previousTask.getStatus()))) {
                            allFinish = false;
                        }
                    }
                }
                if (allFinish) {
                    res.add(nextTask);
                }
            }
        }

        return res;
    }

    @Override
    public List<? extends ICommonTask> findOtherBranchFirstTasksByStatusTask(IStatusTask statusTask) {
        return ((Task) statusTask).getOtherBranchFirstTasks();
    }

    public enum RemoveMode {

        CANCEL, DELETE

    }
}
