package com.synaptix.taskmanager.jpa.converter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ClassConverter implements AttributeConverter<Class<?>, String> {

    private static final Log LOG = LogFactory.getLog(ClassConverter.class);

    @Override
    public String convertToDatabaseColumn(Class<?> aClass) {
        return aClass != null ? aClass.getName() : null;
    }

    @Override
    public Class<?> convertToEntityAttribute(String s) {
        if (StringUtils.isNotBlank(s)) {
            try {
                return Class.forName(s);
            } catch (ClassNotFoundException e) {
                LOG.error("Not found class for " + s, e);
            }
        }
        return null;
    }
}
