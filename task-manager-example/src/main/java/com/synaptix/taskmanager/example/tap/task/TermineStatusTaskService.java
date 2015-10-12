package com.synaptix.taskmanager.example.tap.task;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.tap.TapHelper;
import com.synaptix.taskmanager.example.tap.model.Order;
import com.synaptix.taskmanager.example.tap.model.OrderStatus;
import com.synaptix.taskmanager.jpa.model.Task;

import javax.persistence.EntityManager;

public class TermineStatusTaskService extends AbstractTaskService {

    public TermineStatusTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = (Task) commonTask;

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        Order order = em.find(Order.class, task.getBusinessTaskObjectId());

        em.getTransaction().begin();

        order.setStatus(OrderStatus.TERMINE.name());
        em.persist(order);

        em.getTransaction().commit();

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
