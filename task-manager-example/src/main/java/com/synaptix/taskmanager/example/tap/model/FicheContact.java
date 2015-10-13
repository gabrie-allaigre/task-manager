package com.synaptix.taskmanager.example.tap.model;

import com.synaptix.taskmanager.jpa.model.IBusinessTaskObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.List;

@Entity(name = "FICHE_CONTACT")
public class FicheContact implements IBusinessTaskObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private int version;
    @Column(name = "FICHE_CONTACT_STATUS")
    @Enumerated(EnumType.STRING)
    private FicheContactStatus ficheContactStatus;
    @Column(name = "CLUSTER_ID")
    private Long clusterId;
    @OneToMany(mappedBy = "ficheContact")
    private List<Operation> operations;

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

    public FicheContactStatus getFicheContactStatus() {
        return ficheContactStatus;
    }

    public void setFicheContactStatus(FicheContactStatus ficheContactStatus) {
        this.ficheContactStatus = ficheContactStatus;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return id != null && ((FicheContact) obj).id != null ? id.equals(((FicheContact) obj).id) : super.equals(obj);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("version", version).append("clusterId", clusterId).append("ficheContactStatus", ficheContactStatus)
                .append("operations", operations != null ? operations.size() : 0).build();
    }
}
