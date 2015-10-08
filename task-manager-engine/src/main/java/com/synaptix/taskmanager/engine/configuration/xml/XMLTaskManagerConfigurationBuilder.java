package com.synaptix.taskmanager.engine.configuration.xml;

import com.synaptix.taskmanager.engine.configuration.TaskManagerConfigurationBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskDefinitionRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.ITaskObjectManagerRegistry;
import com.synaptix.taskmanager.engine.configuration.registry.TaskDefinitionRegistryBuilder;
import com.synaptix.taskmanager.engine.configuration.registry.TaskObjectManagerRegistryBuilder;
import com.synaptix.taskmanager.engine.graph.StatusGraphsBuilder;
import com.synaptix.taskmanager.engine.manager.ITaskObjectManager;
import com.synaptix.taskmanager.engine.manager.TaskObjectManagerBuilder;
import com.synaptix.taskmanager.engine.taskdefinition.TaskDefinitionBuilder;
import com.synaptix.taskmanager.engine.taskservice.ITaskService;
import com.synaptix.taskmanager.model.ITaskObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLTaskManagerConfigurationBuilder {

	private TaskManagerConfigurationBuilder taskManagerConfigurationBuilder;

	private Map<Class<?>, ITypeHandler> typeHandlerMap;

	private XMLTaskManagerConfigurationBuilder(InputStream is) throws XMLParseException {
		super();

		this.taskManagerConfigurationBuilder = TaskManagerConfigurationBuilder.newBuilder();
		parseXML(is);
	}

	public static TaskManagerConfigurationBuilder newBuilder(InputStream is) throws XMLParseException {
		return new XMLTaskManagerConfigurationBuilder(is).taskManagerConfigurationBuilder;
	}

	private void parseXML(InputStream is) throws XMLParseException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(is);
		} catch (ParserConfigurationException e) {
			throw new XMLParseException("Not create DocumentBuilder", e);
		} catch (SAXException e) {
			throw new XMLParseException("Not parse document", e);
		} catch (IOException e) {
			throw new XMLParseException("Not read inputStream", e);
		}

		Element root = document.getDocumentElement();

		typeHandlerMap = parseTypeHandlers(getFirstElement(root, "type-handlers"));

		taskManagerConfigurationBuilder.taskObjectManagerRegistry(parseGraphDefinitions(getFirstElement(root, "graph-definitions")));

		taskManagerConfigurationBuilder.taskDefinitionRegistry(parseTaskDefinitions(getFirstElement(root, "task-definitions")));
	}

	private Map<Class<?>, ITypeHandler> parseTypeHandlers(Element typeHandlersElement) throws XMLParseException {
		Map<Class<?>, ITypeHandler> res = new HashMap<>();
		if (typeHandlersElement != null) {
			for (Element typeHandlerElement : getElements(typeHandlersElement, "type-handler")) {
				Class<?> type = stringToClass(typeHandlerElement.getAttribute("type"));
				if (type == null) {
					throw new XMLParseException("type-handler type must not null", null);
				}

				ITypeHandler<?> typeHandler = createInstance(ITypeHandler.class, typeHandlerElement.getAttribute("handler-class"));
				if (typeHandler == null) {
					throw new XMLParseException("type-handler handler-class must not null", null);
				}

				res.put(type, typeHandler);
			}
		}
		return res;
	}

	private ITaskDefinitionRegistry parseTaskDefinitions(Element taskDefinitionsElement) throws XMLParseException {
		TaskDefinitionRegistryBuilder taskDefinitionRegistryBuilder = TaskDefinitionRegistryBuilder.newBuilder();

		if (taskDefinitionsElement != null) {
			for (Element taskDefinitionElement : getElements(taskDefinitionsElement, "task-definition")) {
				String taskId = taskDefinitionElement.getAttribute("task-id");
				if (StringUtils.isBlank(taskId)) {
					throw new XMLParseException("task-id must not empty", null);
				}
				Class<?> taskClass = stringToClass(taskDefinitionElement.getAttribute("task-class"));
				if (taskClass == null) {
					throw new XMLParseException("task-class must not empty", null);
				}
				if (!ITaskService.class.isAssignableFrom(taskClass)) {
					throw new XMLParseException("task-class " + taskClass + " not inherit class=" + ITaskService.class, null);
				}

				List<Pair<Class<?>, String>> parameterStrings = getParameterStrings(taskDefinitionElement);
				int nbParameter = parameterStrings.size();

				List<Constructor<?>> constructors = getConstructors(taskClass, parameterStrings);
				if (constructors.isEmpty()) {
					throw new XMLParseException("task-class " + taskClass + " not found contructor for " + parameterStrings.size() + " parameter(s)", null);
				}
				if (constructors.size() > 1) {
					throw new XMLParseException("task-class " + taskClass + " found multi contructors for " + parameterStrings.size() + " parameter(s) (" + constructors.size() + ")", null);
				}

				Constructor<?> constructor = constructors.get(0);
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				Object[] parameters = new Object[nbParameter];
				for (int i = 0; i < nbParameter; i++) {
					String valueString = parameterStrings.get(i).getRight();
					Class<?> parameterType = parameterTypes[i];

					parameters[i] = convertStringTo(valueString, parameterType);
				}

				ITaskService taskService = null;
				try {
					taskService = (ITaskService) constructor.newInstance(parameters);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					throw new XMLParseException("task-class " + taskClass + " fail to created new instance", e);
				}
				taskDefinitionRegistryBuilder.addTaskDefinition(TaskDefinitionBuilder.newBuilder(taskId, taskService).build());
			}
		}

		return taskDefinitionRegistryBuilder.build();
	}

	private List<Pair<Class<?>, String>> getParameterStrings(Element taskDefinitionElement) throws XMLParseException {
		List<Pair<Class<?>, String>> res = new ArrayList<>();

		List<Element> parameterElements = getElements(taskDefinitionElement, "parameter");
		for (Element parameterElement : parameterElements) {
			res.add(Pair.of(stringToClass(parameterElement.getAttribute("type")), parameterElement.getTextContent()));
		}

		return res;
	}

	private List<Constructor<?>> getConstructors(Class<?> taskClass, List<Pair<Class<?>, String>> parameterStrings) {
		List<Constructor<?>> res = new ArrayList<>();

		int nb = parameterStrings.size();
		for (Constructor<?> constructor : taskClass.getConstructors()) {
			if (constructor.getParameterCount() == nb) {
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				boolean ok = true;
				int i = 0;
				while (i < nb && ok) {
					Pair<Class<?>, String> parameterString = parameterStrings.get(i);
					Class<?> type = parameterString.getLeft();
					Class<?> clazz = parameterTypes[i];

					if (type != null && !type.equals(clazz)) {
						ok = false;
					}

					i++;
				}

				if (ok) {
					res.add(constructor);
				}
			}
		}

		return res;
	}

	private ITaskObjectManagerRegistry parseGraphDefinitions(Element graphDefinitionsElement) throws XMLParseException {
		TaskObjectManagerRegistryBuilder taskObjectManagerRegistryBuilder = TaskObjectManagerRegistryBuilder.newBuilder();

		if (graphDefinitionsElement != null) {
			TaskObjectManagerRegistryBuilder.IInstanceToClass instanceToClass = createInstance(TaskObjectManagerRegistryBuilder.IInstanceToClass.class,
					graphDefinitionsElement.getAttribute("instance-to-class"));
			if (instanceToClass != null) {
				taskObjectManagerRegistryBuilder.instanceToClass(instanceToClass);
			}

			for (Element graphDefinitionElement : getElements(graphDefinitionsElement, "graph-definition")) {
				taskObjectManagerRegistryBuilder.addTaskObjectManager(parseGraphDefinition(graphDefinitionElement));
			}
		}
		return taskObjectManagerRegistryBuilder.build();
	}

	private ITaskObjectManager parseGraphDefinition(Element graphDefinitionElement) throws XMLParseException {
		Class<?> statusClass = stringToClass(graphDefinitionElement.getAttribute("status-class"));
		if (statusClass == null) {
			statusClass = String.class;
		}

		String objectClassString = graphDefinitionElement.getAttribute("object-class");
		Class<?> objectClass = stringToClass(objectClassString);
		if (objectClass == null) {
			throw new XMLParseException("object-class must not null", null);
		}
		if (!ITaskObject.class.isAssignableFrom(objectClass)) {
			throw new XMLParseException("object-class must implements ITaskObject class=" + objectClassString, null);
		}

		TaskObjectManagerBuilder taskObjectManagerBuilder = TaskObjectManagerBuilder.newBuilder((Class<? extends ITaskObject>) objectClass);

		TaskObjectManagerBuilder.IGetStatus<?, ?> getStatus = createInstance(TaskObjectManagerBuilder.IGetStatus.class, graphDefinitionElement.getAttribute("get-status-class"));
		taskObjectManagerBuilder.initialStatus(getStatus);

		Element initStateElement = getFirstElement(graphDefinitionElement, "init-state");
		if (initStateElement != null) {
			Object initStatut = convertStringTo(initStateElement.getAttribute("init-statut"), statusClass);

			StatusGraphsBuilder<Object> statusGraphsBuilder = StatusGraphsBuilder.newBuilder(initStatut);

			getStates(statusGraphsBuilder, initStateElement, statusClass);

			taskObjectManagerBuilder.statusGraphs(statusGraphsBuilder.build());
		}

		Element transitionsElement = getFirstElement(graphDefinitionElement, "transitions");
		if (transitionsElement != null) {
			for (Element transitionElement : getElements(transitionsElement, "transition")) {
				Object startStatus = convertStringTo(transitionElement.getAttribute("start-status"), statusClass);
				Object endStatus = convertStringTo(transitionElement.getAttribute("end-status"), statusClass);
				String tasksPath = transitionElement.getAttribute("tasks-path");

				taskObjectManagerBuilder.addTaskChainCriteria(startStatus, endStatus, tasksPath);
			}
		}
		return taskObjectManagerBuilder.build();
	}

	private Object convertStringTo(String value, Class<?> convertToClass) throws XMLParseException {
		ITypeHandler<?> typeHandler = typeHandlerMap.get(convertToClass);
		if (typeHandler != null) {
			return typeHandler.stringToObject(value);
		}
		if (StringUtils.isBlank(value)) {
			return null;
		}
		if (Integer.class == convertToClass || int.class == convertToClass) {
			return Integer.parseInt(value);
		} else if (Float.class == convertToClass || float.class == convertToClass) {
			return Float.parseFloat(value);
		} else if (Double.class == convertToClass || double.class == convertToClass) {
			return Double.parseDouble(value);
		} else if (String.class == convertToClass) {
			return value;
		} else if (Enum.class.isAssignableFrom(convertToClass)) {
			Class<Enum> e = (Class<Enum>) convertToClass;
			return Enum.valueOf(e, value);
		}
		throw new XMLParseException("Not convert type=" + convertToClass + " for value=" + value, null);
	}

	private Class<?> stringToClass(String classString) throws XMLParseException {
		if (StringUtils.isBlank(classString)) {
			return null;
		}
		switch (classString) {
		case "int":
			return int.class;
		case "float":
			return float.class;
		case "double":
			return double.class;
		}
		try {
			return XMLTaskManagerConfigurationBuilder.class.getClassLoader().loadClass(classString);
		} catch (ClassNotFoundException e) {
			throw new XMLParseException("Not found stringToClass=" + classString, e);
		}
	}

	private <E> E createInstance(Class<E> clazz) throws XMLParseException {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new XMLParseException("Not instanciate class=" + clazz, e);
		}
	}

	private <E> E createInstance(Class<E> inheritClass, String classString) throws XMLParseException {
		Class<?> instanceToClass = stringToClass(classString);
		if (instanceToClass == null) {
			return null;
		}
		if (!inheritClass.isAssignableFrom(instanceToClass)) {
			throw new XMLParseException("Class=" + classString + " not inherit class=" + inheritClass, null);
		}
		return createInstance((Class<E>) instanceToClass);
	}

	private void getStates(StatusGraphsBuilder<Object> res, Element element, Class<?> statusClass) throws XMLParseException {
		for (Element stateElement : getElements(element, "state")) {
			Object status = convertStringTo(stateElement.getAttribute("status"), statusClass);
			String taskId = stateElement.getAttribute("task-id");

			StatusGraphsBuilder<Object> statusGraphsBuilder = StatusGraphsBuilder.newBuilder();
			getStates(statusGraphsBuilder, stateElement, statusClass);

			res.addNextStatusGraph(status, taskId, statusGraphsBuilder);
		}
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
