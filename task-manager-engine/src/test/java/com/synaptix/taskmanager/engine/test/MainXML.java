package com.synaptix.taskmanager.engine.test;

import com.synaptix.taskmanager.engine.configuration.xml.XMLTaskManagerConfigurationBuilder;

public class MainXML {

	public static void main(String[] args) throws Exception {
		XMLTaskManagerConfigurationBuilder.newBuilder(MainXML.class.getResourceAsStream("/config.xml"));
	}
}
