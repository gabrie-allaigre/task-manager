package com.talanlabs.taskmanager.engine.taskservice;

import com.talanlabs.taskmanager.engine.listener.ITaskCycleListener;
import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.talanlabs.taskmanager.model.ITaskCluster;
import com.talanlabs.taskmanager.model.ITaskObject;

public interface ITaskService extends ITaskCycleListener {

    /**
     * Execute service
     *
     * @param context give access task manager engine
     * @param commonTask    task link with service
     * @return result of execution, <use>ExecutionResultBuilder</use>
     */
    IExecutionResult execute(IEngineContext context, ICommonTask commonTask);

    interface IEngineContext {

        ITaskCluster getCurrentTaskCluster();

        ITaskDefinition getTaskDefinition();

        void startEngine(ITaskObject... taskObjects);

        void startEngine(TaskClusterCallback taskClusterCallback, ITaskObject... taskObjects);

        void startEngine(ITaskCluster... taskClusters);

        void addTaskObjectsToTaskCluster(ITaskObject... taskObjects);

        void addTaskObjectsToTaskCluster(ITaskCluster taskCluster, ITaskObject... taskObjects);

        void removeTaskObjectsFromTaskCluster(ITaskObject... taskObjects);

        void moveTaskObjectsToNewTaskCluster(ITaskObject... taskObjects);

        void moveTaskObjectsToNewTaskCluster(TaskClusterCallback taskClusterCallback, ITaskObject... taskObjects);

        void moveTaskObjectsToTaskCluster(ITaskObject... taskObjects);

        void moveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, ITaskObject... taskObjects);

        interface TaskClusterCallback {

            void setTaskCluster(ITaskCluster taskCluster);

        }

    }

    interface IExecutionResult {

        /**
         * Task is finish
         *
         * @return true is finish
         */
        boolean isFinished();

        /**
         * Task no changes business object
         *
         * @return true is no changes
         */
        boolean isNoChanges();

        /**
         * Get result of task, finish or not
         *
         * @return the result
         */
        Object getResult();

        /**
         * Must stop and restart task manager
         *
         * @return true if must stop and restart
         */
        boolean mustStopAndRestartTaskManager();
    }
}
