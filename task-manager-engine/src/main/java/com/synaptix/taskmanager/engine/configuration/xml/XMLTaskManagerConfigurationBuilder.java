package com.synaptix.taskmanager.engine.configuration.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLTaskManagerConfigurationBuilder {

	public void test(InputStream is) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(is);

		Element root = document.getDocumentElement();

		Element graphDefinitionsElement = getFirstElement(root, "graph-definitions");
		if (graphDefinitionsElement != null) {
			String instanceToClassString = graphDefinitionsElement.getAttribute("instance-to-class");
			System.out.println(instanceToClassString);

			getElements(graphDefinitionsElement, "graph-definition").forEach(graphDefinitionElement -> {
				String statusClass = graphDefinitionElement.getAttribute("status-class");
				System.out.println(statusClass);

				String objectClass = graphDefinitionElement.getAttribute("object-class");
				System.out.println(objectClass);

				Element initStateElement = getFirstElement(graphDefinitionElement, "init-state");
				if (initStateElement != null) {
					String initStatut = initStateElement.getAttribute("init-statut");
					System.out.println(initStatut);

					getStates(initStateElement);
				}

				Element transitionsElement = getFirstElement(graphDefinitionElement, "transitions");
				if (transitionsElement != null) {
					getElements(transitionsElement, "transition").forEach(transitionElement -> {
						String startStatus = transitionElement.getAttribute("start-status");
						String endStatus = transitionElement.getAttribute("end-status");
						String tasksPath = transitionElement.getAttribute("tasks-path");

						System.out.println("Transition " + startStatus + " " + endStatus + " " + tasksPath);
					});
				}
			});
		}

		Element taskDefinitionsElement = getFirstElement(root, "task-definitions");
		if (taskDefinitionsElement != null) {
			getElements(taskDefinitionsElement, "task-definition").forEach(taskDefinitionElement -> {
				String taskId = taskDefinitionElement.getAttribute("task-id");
				String taskClass = taskDefinitionElement.getAttribute("task-class");

				System.out.println("TaskDefinition " + taskId + " " + taskClass);
			});
		}
	}

	private void getStates(Element element) {
		getElements(element, "state").forEach(stateElement -> {
			String status = stateElement.getAttribute("status");
			String taskId = stateElement.getAttribute("task-id");

			System.out.println("State " + status + " " + taskId);

			getStates(stateElement);
		});
	}

	private List<Element> getElements(Element element, String tagName) {
		List<Element> res = new ArrayList<>();

		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) node;
				if (tagName.equals(e.getTagName())) {
					res.add(e);
				}
			}
		}

		return res;
	}

	private Element getFirstElement(Element element, String tagName) {
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) node;
				if (tagName.equals(e.getTagName())) {
					return e;
				}
			}
		}

		return null;
	}
}
