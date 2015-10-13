package com.synaptix.taskmanager.example.tap.task.fiche;

import com.synaptix.taskmanager.example.tap.model.FicheContactStatus;

public class CommandeStatusTaskService extends AbstractFicheStatusTaskService {

    public CommandeStatusTaskService() {
        super(FicheContactStatus.COMMANDE);
    }


}
