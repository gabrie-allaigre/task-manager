package com.synaptix.taskmanager.jpa.test.unit;

import com.synaptix.taskmanager.jpa.converter.ClassConverter;
import org.junit.Assert;
import org.junit.Test;

public class ClassConverterTest {

	@Test
	public void test1() {
		Assert.assertEquals(new ClassConverter().convertToDatabaseColumn(String.class), "java.lang.String");
	}

	@Test
	public void test2() {
		Assert.assertEquals(new ClassConverter().convertToEntityAttribute("java.lang.String"),String.class);
	}

	@Test
	public void test3() {
		Assert.assertNull(new ClassConverter().convertToEntityAttribute("je.nexiste.pas.Moi"));
	}
}
