package com.synaptix.taskmanager.guice.test.unit;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import org.junit.Test;

public class GuiceTest {

	@Test
	public void test1() {
		new AbstractModule() {

			@Override
			protected void configure() {
				MapBinder.newMapBinder(binder(),String.class,String.class).addBinding("toto").toInstance("rien");
			}
		};
	}
}
