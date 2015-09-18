package com.talanlabs.taskmanager.component.test.unit;

import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.taskmanager.component.ComponentInstanceToClass;
import com.talanlabs.taskmanager.component.test.data.ExecutionOrder;
import com.talanlabs.taskmanager.component.test.data.ICustomerOrder;
import com.talanlabs.taskmanager.component.test.data.ISuperCustomerOrder;
import org.junit.Assert;
import org.junit.Test;

public class ComponentInstanceToClassTest {

    @Test
    public void test1() {
        ICustomerOrder customerOrder = ComponentFactory.getInstance().createInstance(ICustomerOrder.class);

        Assert.assertSame(ICustomerOrder.class, ComponentInstanceToClass.INSTANCE.instanceToClass(customerOrder));
    }

    @Test
    public void test2() {
        ISuperCustomerOrder superCustomerOrder = ComponentFactory.getInstance().createInstance(ISuperCustomerOrder.class);

        Assert.assertSame(ISuperCustomerOrder.class, ComponentInstanceToClass.INSTANCE.instanceToClass(superCustomerOrder));
        Assert.assertNotSame(ICustomerOrder.class, ComponentInstanceToClass.INSTANCE.instanceToClass(superCustomerOrder));
    }

    @Test
    public void test3() {
        ExecutionOrder executionOrder = new ExecutionOrder();

        Assert.assertEquals(ExecutionOrder.class, ComponentInstanceToClass.INSTANCE.instanceToClass(executionOrder));
    }
}
