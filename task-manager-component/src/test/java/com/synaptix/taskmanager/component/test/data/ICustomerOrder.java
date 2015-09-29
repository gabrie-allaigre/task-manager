package com.synaptix.taskmanager.component.test.data;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.taskmanager.model.ITaskObject;

import java.util.Date;

@SynaptixComponent
public interface ICustomerOrder extends IComponent,ITaskObject {

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
