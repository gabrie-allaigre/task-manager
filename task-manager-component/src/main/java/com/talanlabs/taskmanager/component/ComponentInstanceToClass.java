package com.talanlabs.taskmanager.component;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.Proxy;
import com.talanlabs.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.talanlabs.taskmanager.model.ITaskObject;

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
