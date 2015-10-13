package com.synaptix.taskmanager.example.tap.task;

import com.synaptix.taskmanager.engine.task.ICommonTask;
import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.engine.taskservice.AbstractTaskService;
import com.synaptix.taskmanager.example.tap.TapHelper;
import com.synaptix.taskmanager.example.tap.model.FicheContact;
import com.synaptix.taskmanager.example.tap.model.Item;
import com.synaptix.taskmanager.jpa.model.Task;
import com.synaptix.taskmanager.model.ITaskCluster;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public abstract class AbstractItemTaskService extends AbstractTaskService {

    private final String type;

    protected AbstractItemTaskService(String type) {
        super();

        this.type = type;
    }

    @Override
    public void onTodo(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask commonTask) {
        Task task = (Task) commonTask;

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        FicheContact ficheContact = em.find(FicheContact.class, task.getBusinessTaskObjectId());

        em.getTransaction().begin();

        Item item = new Item();
        item.setType(type);
        item.setDone(false);
        item.setFicheContact(ficheContact);
        item.setTask(task);

        em.persist(item);

        em.getTransaction().commit();
    }

    @Override
    public void onDone(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask commonTask) {
        Task task = (Task) commonTask;

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        FicheContact ficheContact = em.find(FicheContact.class, task.getBusinessTaskObjectId());

        em.getTransaction().begin();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> cq = cb.createQuery(Item.class);
        Root<Item> itemRoot = cq.from(Item.class);
        cq.where(cb.equal(itemRoot.get("task"), task));

        TypedQuery<Item> q = em.createQuery(cq);
        q.getResultList().forEach(item -> {
            item.setDone(true);
            item.setDoneFicheContactStatus(ficheContact.getFicheContactStatus());
            em.persist(item);
        });

        em.getTransaction().commit();
    }

    @Override
    public void onDelete(ITaskCluster taskCluster, ITaskDefinition taskDefinition, ICommonTask commonTask) {
        Task task = (Task) commonTask;

        EntityManager em = TapHelper.getInstance().getJpaAccess().getEntityManager();

        em.getTransaction().begin();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Item> cq = cb.createQuery(Item.class);
        Root<Item> itemRoot = cq.from(Item.class);
        cq.where(cb.equal(itemRoot.get("task"), task));

        TypedQuery<Item> q = em.createQuery(cq);
        q.getResultList().forEach(item -> em.remove(item));

        em.getTransaction().commit();
    }
}


