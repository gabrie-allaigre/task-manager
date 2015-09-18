package com.talanlabs.taskmanager.engine.test.data;

import java.util.Date;

public class BusinessObject extends AbstractCommonObject {

    private Date date;

    private OptionObject optionObject;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public OptionObject getOptionObject() {
        return optionObject;
    }

    public void setOptionObject(OptionObject optionObject) {
        this.optionObject = optionObject;
    }
}
