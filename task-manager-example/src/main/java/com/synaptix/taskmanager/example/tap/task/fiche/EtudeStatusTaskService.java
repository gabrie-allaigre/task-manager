package com.synaptix.taskmanager.example.tap.task.fiche;

import com.synaptix.taskmanager.example.tap.model.FicheContactStatus;

public class EtudeStatusTaskService extends AbstractFicheStatusTaskService {

    public EtudeStatusTaskService() {
        super(FicheContactStatus.ETUDE);
    }

}
