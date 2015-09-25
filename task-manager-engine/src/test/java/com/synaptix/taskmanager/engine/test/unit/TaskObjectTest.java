package com.synaptix.taskmanager.engine.test.unit;

import org.junit.Assert;
import org.junit.Test;

import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.test.data.BusinessObject;
import com.synaptix.taskmanager.engine.test.data.OtherBusinessObject;

public class TaskObjectTest {

	@Test
	public void test1() {
		ITaskObjectManager<BusinessObject> taskObjectManager = TaskObjectManagerBuilder.newBuilder(BusinessObject.class).build();

		Assert.assertEquals(taskObjectManager.getTaskObjectClass(), BusinessObject.class);
		Assert.assertNull(taskObjectManager.getTaskChainCriteria(null, null, null));
	}

	@Test
	public void test2() {
		ITaskObjectManager<BusinessObject> taskObjectManager = TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB").build();

		Assert.assertEquals(taskObjectManager.getTaskChainCriteria(null, null, "A"), "VERSA->VERSB");
	}

	@Test
	public void test3() {
		ITaskObjectManager<BusinessObject> taskObjectManager = TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB").build();

		Assert.assertEquals(taskObjectManager.getTaskChainCriteria(null, null, "A"), "VERSA->VERSB");
	}

	@Test
	public void test4() {
		ITaskObjectManager<BusinessObject> taskObjectManager = TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB")
				.addTaskChainCriteria("B", "C", "VERSC").build();

		Assert.assertEquals(taskObjectManager.getTaskChainCriteria(null, "B", "C"), "VERSC");
	}

	@Test
	public void test5() {
		ITaskObjectManager<BusinessObject> taskObjectManager = TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB")
				.addTaskChainCriteria("B", "C", "VERSC").build();

		ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(taskObjectManager).build();

		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(BusinessObject.class), taskObjectManager);
		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(new BusinessObject()), taskObjectManager);
	}

	@Test
	public void test6() {
		ITaskObjectManager<BusinessObject> taskObjectManager = TaskObjectManagerBuilder.newBuilder(BusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB")
				.addTaskChainCriteria("B", "C", "VERSC").build();
		ITaskObjectManager<OtherBusinessObject> otherTaskObjectManager = TaskObjectManagerBuilder.newBuilder(OtherBusinessObject.class).addTaskChainCriteria(null, "A", "VERSA->VERSB").build();

		ITaskObjectManagerRegistry taskObjectManagerRegistry = TaskObjectManagerRegistryBuilder.newBuilder().addTaskObjectManager(taskObjectManager).addTaskObjectManager(otherTaskObjectManager)
				.build();

		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(BusinessObject.class), taskObjectManager);
		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(new BusinessObject()), taskObjectManager);

		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(OtherBusinessObject.class), otherTaskObjectManager);
		Assert.assertEquals(taskObjectManagerRegistry.getTaskObjectManager(new OtherBusinessObject()), otherTaskObjectManager);
	}
}
