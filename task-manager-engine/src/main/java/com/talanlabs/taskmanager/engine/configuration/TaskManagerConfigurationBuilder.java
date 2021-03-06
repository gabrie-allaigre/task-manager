package com.talanlabs.taskmanager.engine.configuration;

import com.talanlabs.taskmanager.engine.configuration.factory.ITaskFactory;
import com.talanlabs.taskmanager.engine.configuration.persistance.ITaskManagerReader;
import com.talanlabs.taskmanager.engine.configuration.persistance.ITaskManagerWriter;
import com.talanlabs.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.talanlabs.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.talanlabs.taskmanager.engine.configuration.transform.DefaultTaskChainCriteriaTransform;
import com.talanlabs.taskmanager.engine.configuration.transform.ITaskChainCriteriaTransform;
import com.talanlabs.taskmanager.engine.memory.MemoryTaskManagerReaderWriter;
import com.talanlabs.taskmanager.engine.memory.SimpleTaskFactory;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TaskManagerConfigurationBuilder {

    private TaskManagerConfigurationImpl taskManagerConfiguration;

    private TaskManagerConfigurationBuilder() {
        super();

        this.taskManagerConfiguration = new TaskManagerConfigurationImpl();
    }

    public static TaskManagerConfigurationBuilder newBuilder() {
        return new TaskManagerConfigurationBuilder();
    }

    public TaskManagerConfigurationBuilder taskObjectManagerRegistry(ITaskObjectManagerRegistry taskObjectManagerRegistry) {
        taskManagerConfiguration.taskObjectManagerRegistry = taskObjectManagerRegistry;
        return this;
    }

    public TaskManagerConfigurationBuilder taskDefinitionRegistry(ITaskDefinitionRegistry taskDefinitionRegistry) {
        taskManagerConfiguration.taskDefinitionRegistry = taskDefinitionRegistry;
        return this;
    }

    public TaskManagerConfigurationBuilder taskFactory(ITaskFactory taskFactory) {
        taskManagerConfiguration.taskFactory = taskFactory;
        return this;
    }

    public TaskManagerConfigurationBuilder taskChainCriteriaBuilder(ITaskChainCriteriaTransform taskChainCriteriaBuilder) {
        taskManagerConfiguration.taskChainCriteriaBuilder = taskChainCriteriaBuilder;
        return this;
    }

    public TaskManagerConfigurationBuilder taskManagerReader(ITaskManagerReader taskManagerReader) {
        taskManagerConfiguration.taskManagerReader = taskManagerReader;
        return this;
    }

    public TaskManagerConfigurationBuilder taskManagerWriter(ITaskManagerWriter taskManagerWriter) {
        taskManagerConfiguration.taskManagerWriter = taskManagerWriter;
        return this;
    }

    public ITaskManagerConfiguration build() {
        return taskManagerConfiguration;
    }

    private static class TaskManagerConfigurationImpl implements ITaskManagerConfiguration {

        private ITaskObjectManagerRegistry taskObjectManagerRegistry;

        private ITaskDefinitionRegistry taskDefinitionRegistry;

        private ITaskFactory taskFactory;

        private ITaskChainCriteriaTransform taskChainCriteriaBuilder;

        private ITaskManagerReader taskManagerReader;

        private ITaskManagerWriter taskManagerWriter;

        public TaskManagerConfigurationImpl() {
            super();

            this.taskFactory = new SimpleTaskFactory();
            this.taskChainCriteriaBuilder = new DefaultTaskChainCriteriaTransform();

            MemoryTaskManagerReaderWriter memoryTaskReaderWriter = new MemoryTaskManagerReaderWriter();
            this.taskManagerReader = memoryTaskReaderWriter;
            this.taskManagerWriter = memoryTaskReaderWriter;

        }

        @Override
        public ITaskObjectManagerRegistry getTaskObjectManagerRegistry() {
            return taskObjectManagerRegistry;
        }

        @Override
        public ITaskDefinitionRegistry getTaskDefinitionRegistry() {
            return taskDefinitionRegistry;
        }

        @Override
        public ITaskFactory getTaskFactory() {
            return taskFactory;
        }

        @Override
        public ITaskChainCriteriaTransform getTaskChainCriteriaBuilder() {
            return taskChainCriteriaBuilder;
        }

        @Override
        public ITaskManagerReader getTaskManagerReader() {
            return taskManagerReader;
        }

        @Override
        public ITaskManagerWriter getTaskManagerWriter() {
            return taskManagerWriter;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
