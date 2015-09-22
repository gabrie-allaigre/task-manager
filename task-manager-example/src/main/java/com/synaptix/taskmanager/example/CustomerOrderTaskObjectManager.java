package com.synaptix.taskmanager.example;

import com.synaptix.taskmanager.manager.AbstractTaskObjectManager;
import com.synaptix.taskmanager.model.ITask;

public class CustomerOrderTaskObjectManager extends AbstractTaskObjectManager<ICustomerOrder> {

	public CustomerOrderTaskObjectManager() {
		super(ICustomerOrder.class);
	}

	@Override
	public String getTaskChainCriteria(ITask task) {
		// if (CustomerOrderStatus.TCO.name().equals(task.getPreviousStatus()) && CustomerOrderStatus.VAL.name().equals(task.getNextStatus())) {
		// return "REF";
		// } else if (CustomerOrderStatus.VAL.name().equals(task.getPreviousStatus()) && CustomerOrderStatus.CLO.name().equals(task.getNextStatus())) {
		// return "DATE";
		// }
		return "";
	}
}
