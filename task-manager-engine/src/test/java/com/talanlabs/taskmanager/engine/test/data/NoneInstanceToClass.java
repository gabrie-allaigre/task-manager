package com.talanlabs.taskmanager.engine.test.data;

import com.talanlabs.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.talanlabs.taskmanager.model.ITaskObject;

public class NoneInstanceToClass implements TaskObjectManagerRegistryBuilder.IInstanceToClass {

    @Override
    public <F extends ITaskObject> Class<F> instanceToClass(F taskObject) {
        return (Class<F>) BusinessObject.class;
    }
}
