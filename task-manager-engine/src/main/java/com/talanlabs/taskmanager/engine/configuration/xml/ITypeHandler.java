package com.talanlabs.taskmanager.engine.configuration.xml;

public interface ITypeHandler<E> {

    E stringToObject(String valueString) throws XMLParseException;

}
