package com.talanlabs.taskmanager.engine.manager;

import com.talanlabs.taskmanager.engine.graph.IStatusGraph;
import com.talanlabs.taskmanager.engine.task.IStatusTask;
import com.talanlabs.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskObjectManagerBuilder<E, F extends ITaskObject> {

    private MyTaskObjectManager<E, F> taskObjectManager;

    private TaskObjectManagerBuilder(Class<F> taskObjectClass) {
        super();

        this.taskObjectManager = new MyTaskObjectManager<>(taskObjectClass);
    }

    public static <E, F extends ITaskObject> TaskObjectManagerBuilder<E, F> newBuilder(Class<F> taskObjectClass) {
        return new TaskObjectManagerBuilder<>(taskObjectClass);
    }

    public TaskObjectManagerBuilder<E, F> initialStatus(E defaultInitialStatus) {
        taskObjectManager.defaultInitialStatus = defaultInitialStatus;
        return this;
    }

    public TaskObjectManagerBuilder<E, F> initialStatus(IGetStatus<E, F> getStatus) {
        taskObjectManager.getStatus = getStatus;
        return this;
    }

    public TaskObjectManagerBuilder<E, F> statusGraphs(List<IStatusGraph<E>> statusGraphs) {
        taskObjectManager.statusGraphs = statusGraphs;
        return this;
    }

    public TaskObjectManagerBuilder<E, F> addTaskChainCriteria(E currentStatus, E nextStatus, String taskChainCriteria) {
        taskObjectManager.taskChainCriteriaMap.put(Pair.of(currentStatus, nextStatus), taskChainCriteria);
        return this;
    }

    public ITaskObjectManager<E, F> build() {
        return taskObjectManager;
    }

    public interface IGetStatus<E, F extends ITaskObject> {

        E getStatus(F taskObject);

    }

    private static class MyTaskObjectManager<E, F extends ITaskObject> extends AbstractTaskObjectManager<E, F> {

        private final Map<Pair<E, E>, String> taskChainCriteriaMap;

        private List<IStatusGraph<E>> statusGraphs;

        private E defaultInitialStatus;

        private IGetStatus<E, F> getStatus;

        public MyTaskObjectManager(Class<F> taskObjectClass) {
            super(taskObjectClass);

            this.taskChainCriteriaMap = new HashMap<>();
        }

        @Override
        public E getInitialStatus(F taskObject) {
            if (getStatus != null) {
                return getStatus.getStatus(taskObject);
            }
            return defaultInitialStatus;
        }

        @Override
        public List<IStatusGraph<E>> getNextStatusGraphsByTaskObjectType(IStatusTask statusTask, E currentStatus) {
            List<IStatusGraph<E>> res = new ArrayList<>();
            if (statusGraphs != null && !statusGraphs.isEmpty()) {
                res.addAll(statusGraphs.stream()
                        .filter(statusGraph -> (currentStatus == null && statusGraph.getPreviousStatus() == null) || (currentStatus != null && currentStatus.equals(statusGraph.getPreviousStatus())))
                        .collect(Collectors.toList()));
            }
            return res;
        }

        @Override
        public String getTaskChainCriteria(IStatusTask statusTask, E currentStatus, E nextStatus) {
            return taskChainCriteriaMap.get(Pair.of(currentStatus, nextStatus));
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
