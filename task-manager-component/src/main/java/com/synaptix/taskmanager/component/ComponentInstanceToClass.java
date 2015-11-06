package com.synaptix.taskmanager.component;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.Proxy;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.model.ITaskObject;

public class ComponentInstanceToClass implements TaskObjectManagerRegistryBuilder.IInstanceToClass {

    public static final ComponentInstanceToClass INSTANCE = new ComponentInstanceToClass();

    @Override
    public <F extends ITaskObject> Class<F> instanceToClass(F taskObject) {
        if (taskObject instanceof IComponent) {
            return (Class<F>) ((Proxy) taskObject).straightGetComponentClass();
        }
        return (Class<F>) taskObject.getClass();
    }
}
