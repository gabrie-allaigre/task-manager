package com.talanlabs.taskmanager.example.tap.task.fiche;

import com.talanlabs.taskmanager.example.tap.model.FicheContactStatus;

public class CommandeStatusTaskService extends AbstractFicheStatusTaskService {

    public CommandeStatusTaskService() {
        super(FicheContactStatus.COMMANDE);
    }


}
