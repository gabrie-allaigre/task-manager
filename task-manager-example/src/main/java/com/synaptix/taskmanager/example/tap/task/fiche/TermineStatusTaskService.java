package com.synaptix.taskmanager.example.tap.task.fiche;

import com.synaptix.taskmanager.example.tap.model.FicheContactStatus;

public class TermineStatusTaskService extends AbstractFicheStatusTaskService {

    public TermineStatusTaskService() {
        super(FicheContactStatus.TERMINE);
    }

}
