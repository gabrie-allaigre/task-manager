package com.synaptix.taskmanager.engine.test.unit;

import com.synaptix.taskmanager.engine.configuration.ITaskManagerConfiguration;
import com.synaptix.taskmanager.engine.configuration.xml.XMLParseException;
import com.synaptix.taskmanager.engine.configuration.xml.XMLTaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.test.data.BusinessObject;
import com.synaptix.taskmanager.engine.test.data.MultiUpdateStatusTaskService;
import org.junit.Assert;
import org.junit.Test;

public class XMLTaskManagerConfigurationTest {

	@Test
	public void test1() {
		ITaskManagerConfiguration taskManagerConfiguration = null;
		try {
			taskManagerConfiguration = XMLTaskManagerConfigurationBuilder.newBuilder(XMLTaskManagerConfigurationTest.class.getResourceAsStream("config1.xml")).build();
		} catch (XMLParseException e) {
			e.printStackTrace();
		}

		Assert.assertNotNull(taskManagerConfiguration);
		Assert.assertNotNull(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class));
		Assert.assertEquals(taskManagerConfiguration.getTaskObjectManagerRegistry().getTaskObjectManager(BusinessObject.class).getNextStatusGraphsByTaskObjectType(null, null).size(), 1);

		Assert.assertNotNull(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK"));
		Assert.assertEquals(taskManagerConfiguration.getTaskDefinitionRegistry().getTaskDefinition("A_TASK").getTaskService().getClass(), MultiUpdateStatusTaskService.class);
	}
}
