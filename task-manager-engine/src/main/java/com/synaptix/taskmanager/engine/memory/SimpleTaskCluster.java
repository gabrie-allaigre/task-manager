package com.synaptix.taskmanager.engine.memory;

import com.synaptix.taskmanager.model.ITaskCluster;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SimpleTaskCluster implements ITaskCluster {

    private boolean checkGraphCreated;

    private boolean checkArchived;

    @Override
    public boolean isCheckGraphCreated() {
        return this.checkGraphCreated;
    }

    public void setCheckGraphCreated(boolean checkGraphCreated) {
        this.checkGraphCreated = checkGraphCreated;
    }

    @Override
    public boolean isCheckArchived() {
        return this.checkArchived;
    }

    public void setCheckArchived(boolean checkArchived) {
        this.checkArchived = checkArchived;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
