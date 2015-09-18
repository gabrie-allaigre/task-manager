package com.synaptix.taskmanager.engine.configuration.result;

import com.synaptix.component.model.IStackResult;

public interface ITaskResultDetailBuilder {

	public String buildStack(IStackResult stackResult, int maxResultDepth);

}
