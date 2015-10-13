package com.synaptix.taskmanager.example.tap;

import com.synaptix.taskmanager.engine.taskdefinition.ITaskDefinition;
import com.synaptix.taskmanager.example.tap.model.FicheContactStatus;

public interface ITapTaskDefinition extends ITaskDefinition {

    String getType();

    FicheContactStatus getEndFicheContactStatus();

}
