package com.talanlabs.taskmanager.example.component.business;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.taskmanager.model.ITaskObject;

import java.util.Date;

@ComponentBean
public interface ICustomerOrder extends ITaskObject, IComponent {

    String getId();

    void setId(String id);

    int getVersion();

    void setVersion(int version);

    CustomerOrderStatus getStatus();

    void setStatus(CustomerOrderStatus status);

    String getCustomerOrderNo();

    void setCustomerOrderNo(String customerOrderNo);

    String getReference();

    void setReference(String reference);

    boolean isConfirmed();

    void setConfirmed(boolean confirmed);

    boolean isCancelled();

    void setCancelled(boolean cancelled);

    Date getDateClosed();

    void setDateClosed(Date dateClosed);

}
