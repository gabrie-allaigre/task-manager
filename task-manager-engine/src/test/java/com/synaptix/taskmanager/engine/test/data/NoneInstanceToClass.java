package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.model.ITaskObject;

public class NoneInstanceToClass implements TaskObjectManagerRegistryBuilder.IInstanceToClass {

    @Override
    public <F extends ITaskObject> Class<F> instanceToClass(F taskObject) {
        return (Class<F>) BusinessObject.class;
    }
}
