package com.talanlabs.taskmanager.example.tap;

import com.talanlabs.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.talanlabs.taskmanager.example.tap.model.FicheContactStatus;

public interface ITapTaskDefinition extends ITaskDefinition {

    String getType();

    FicheContactStatus getEndFicheContactStatus();

}
