package com.talanlabs.taskmanager.engine.configuration.transform;

import com.talanlabs.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.talanlabs.taskmanager.engine.task.ISubTask;

import java.util.List;
import java.util.Map;

public interface ITaskChainCriteriaTransform {

    /**
     * Trasnform chain to tasks
     *
     * @param taskManagerConfiguration configuration
     * @param taskChainCriteria        criteria
     * @return result with all tasks
     */
    IResult transformeToTasks(ITaskManagerConfiguration taskManagerConfiguration, String taskChainCriteria);

    interface IResult {

        List<ISubTask> getNewSubTasks();

        List<ISubTask> getNextSubTasks();

        Map<ISubTask, List<ISubTask>> getLinkNextTasksMap();

    }
}
