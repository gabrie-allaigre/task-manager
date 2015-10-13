package com.synaptix.taskmanager.example.tap.model;

import com.synaptix.taskmanager.jpa.model.IBusinessTaskObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
public class Operation implements IBusinessTaskObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private int version;
    @Column(name = "OPERATION_STATUS")
    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus;
    private String type;
    @Column(name = "CLUSTER_ID")
    private Long clusterId;
    @ManyToOne
    private FicheContact ficheContact;
    @Column(name = "END_FICHE_CONTACT_STATUS")
    @Enumerated(EnumType.STRING)
    private FicheContactStatus endFicheContactStatus;

    @Override
    public final Long getId() {
        return id;
    }

    @Override
    public Long getClusterId() {
        return clusterId;
    }

    @Override
    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(OperationStatus operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FicheContact getFicheContact() {
        return ficheContact;
    }

    public void setFicheContact(FicheContact ficheContact) {
        this.ficheContact = ficheContact;
    }

    public FicheContactStatus getEndFicheContactStatus() {
        return endFicheContactStatus;
    }

    public void setEndFicheContactStatus(FicheContactStatus endFicheContactStatus) {
        this.endFicheContactStatus = endFicheContactStatus;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return id != null && ((Operation) obj).id != null ? id.equals(((Operation) obj).id) : super.equals(obj);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("version", version).append("clusterId", clusterId).append("operationStatus", operationStatus).append("type", type)
                .append("ficheContact", ficheContact != null ? ficheContact.getId() : null).append("endFicheContactStatus", endFicheContactStatus).build();
    }
}
