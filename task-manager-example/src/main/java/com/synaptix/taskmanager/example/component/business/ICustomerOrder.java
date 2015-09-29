package com.synaptix.taskmanager.example.component.business;

import java.util.Date;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.taskmanager.model.ITaskObject;

@SynaptixComponent
public interface ICustomerOrder extends IEntity, ITaskObject {

	public CustomerOrderStatus getStatus();

	public void setStatus(CustomerOrderStatus status);

	public String getCustomerOrderNo();

	public void setCustomerOrderNo(String customerOrderNo);

	public String getReference();

	public void setReference(String reference);

	public boolean isConfirmed();

	public void setConfirmed(boolean confirmed);

	public boolean isCancelled();

	public void setCancelled(boolean cancelled);

	public Date getDateClosed();

	public void setDateClosed(Date dateClosed);

}
