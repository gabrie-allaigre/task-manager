package com.synaptix.taskmanager.engine.test.data;

import com.synaptix.taskmanager.engine.configuration.xml.AbstractTypeHandler;
import com.synaptix.taskmanager.engine.configuration.xml.XMLParseException;
import org.apache.commons.lang3.StringUtils;

public class MyStatusObjectTypeHandler extends AbstractTypeHandler<MyStatusObject> {

    @Override
    public MyStatusObject stringToObject(String valueString) throws XMLParseException {
        if (StringUtils.isBlank(valueString)) {
            return null;
        }
        switch (valueString) {
        case "A":
            return MyStatusObject.A;
        case "B":
            return MyStatusObject.B;
        }
        throw new XMLParseException("Not found", null);
    }

}
