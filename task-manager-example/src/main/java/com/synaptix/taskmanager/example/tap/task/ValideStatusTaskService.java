package com.synaptix.taskmanager.example.tap.task;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.engine.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.example.tap.TapHelper;
import com.synaptix.taskmanager.example.tap.model.FicheContact;
import com.synaptix.taskmanager.example.tap.model.FicheContactStatus;
import com.synaptix.taskmanager.jpa.model.Task;

import javax.persistence.EntityManager;

public class ValideStatusTaskService extends AbstractTaskService {

    public ValideStatusTaskService() {
        super();
    }

    @Override
    public IExecutionResult execute(IEngineContext context, ICommonTask commonTask) {
        Task task = (Task) commonTask;

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        FicheContact ficheContact = em.find(FicheContact.class, task.getBusinessTaskObjectId());

        em.getTransaction().begin();

        ficheContact.setFicheContactStatus(FicheContactStatus.VALIDE);
        em.persist(ficheContact);

        em.getTransaction().commit();

        return ExecutionResultBuilder.newBuilder().finished();
    }
}
