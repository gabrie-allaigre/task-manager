package com.synaptix.taskmanager.component.test.data;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.Date;

@SynaptixComponent
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
