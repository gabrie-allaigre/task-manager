package com.synaptix.taskmanager.example;

import com.synaptix.taskmanager.manager.AbstractTaskObjectManager;
import com.synaptix.taskmanager.model.ITask;

public class CustomerOrderObjectTypeTaskFactory extends AbstractTaskObjectManager<ICustomerOrder> {

	public CustomerOrderObjectTypeTaskFactory() {
		super(ICustomerOrder.class);
	}

	@Override
	public String getTaskChainCriteria(ITask task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getExecutant(ITask task) {
		return new UserBuilder().name("Gabriel").build();
	}

	@Override
	public Object getManager(ITask task) {
		return new UserBuilder().name("Sandra").build();
	}
}
