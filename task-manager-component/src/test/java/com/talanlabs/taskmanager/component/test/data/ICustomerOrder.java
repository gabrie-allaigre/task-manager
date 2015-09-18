package com.talanlabs.taskmanager.component.test.data;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.taskmanager.model.ITaskObject;

import java.util.Date;

@ComponentBean
public interface ICustomerOrder extends IComponent, ITaskObject {

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
