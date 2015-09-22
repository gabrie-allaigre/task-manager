package com.synaptix.taskmanager.example;

import com.synaptix.taskmanager.manager.AbstractTaskObjectManager;
import com.synaptix.taskmanager.manager.UpdateStatusTask;

public class CustomerOrderTaskObjectManager extends AbstractTaskObjectManager<ICustomerOrder> {

	public CustomerOrderTaskObjectManager() {
		super(ICustomerOrder.class);
	}

	@Override
	public String getTaskChainCriteria(UpdateStatusTask updateStatusTask, Object currentStatus, Object nextStatus) {
		if (currentStatus == null && CustomerOrderStatus.TCO.equals(nextStatus)) {
			return "REF";
		} else if (CustomerOrderStatus.VAL.equals(currentStatus) && CustomerOrderStatus.CLO.equals(nextStatus)) {
			return "DATE";
		}
		return "";
	}
}
