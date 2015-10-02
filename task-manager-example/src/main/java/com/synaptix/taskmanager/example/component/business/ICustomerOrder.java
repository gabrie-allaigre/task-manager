package com.synaptix.taskmanager.example.component.business;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.Date;

@SynaptixComponent
public interface ICustomerOrder extends IEntity, ITaskObject {

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
