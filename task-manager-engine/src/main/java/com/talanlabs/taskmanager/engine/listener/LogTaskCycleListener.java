package com.talanlabs.taskmanager.engine.listener;

import com.talanlabs.taskmanager.engine.task.ICommonTask;
import com.talanlabs.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.talanlabs.taskmanager.model.ITaskCluster;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogTaskCycleListener extends AbstractTaskCycleListener {

    private static final Log LOG = LogFactory.getLog(LogTaskCycleListener.class);

    @Override
    public void onTodo(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("TM - onTodo " + task);
        }
    }

    @Override
    public void onCurrent(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("TM - onCurrent " + task);
        }
    }

    @Override
    public void onNothing(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("TM - onNothing " + task);
        }
    }

    @Override
    public void onDone(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("TM - onDone " + task);
        }
    }

    @Override
    public void onDelete(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask task) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("TM - onDelete " + task);
        }
    }
}
