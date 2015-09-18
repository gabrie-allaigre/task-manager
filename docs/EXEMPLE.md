# Exemple

Projet TAP

# Objets métiers

## Fiche Contact (FC)

La fiche contact est la commande d'un client pour un transport

### FicheContactStatut

| Statut |
| ------------ |
| Init |
| Validé |
| Clos |

### FicheContact

| Colonne | Type |
| ------- | ---- |
| id | Long |
| ficheContactStatut | FicheContactStatut |
| origine | ... |
| destination | ... |
| ... | ... |

## Dossier Transport (DT)

### DossierTransportStatut

| Statut |
| ------------ |
| Init |
| En concertation |
| A l'étude |
| A assembler |
| A annoncer |
| Conformité à saisir |
| Clos |

### DossierTransport

| Colonne | Type |
| ------- | ---- |
| id | Long |
| dossierTransportStatut | DossierTransportStatut |
| ficheContact | FicheContact |

## Opération (Op)

### OperationStatut

| Statut |
| ------------ |
| Init |
| Fin |

# Tables de critères

## FicheContactStatusTasksCriteria

| Statut Courant | Statut Suivant | Statut Task Suivant |
| :------------: | :------------: | ------------------- |
| Init | Validé | ValideFCStatusTask |
| Validé | Clos | ClosFCStatusTask |

## FicheContactSubTasksCriteria

| Statut Courant | Statut Suivant | SubTasks |
| :------------: | :------------: | -------- |
| Init | Validé |  |
| Validé | Clos | CreateDossierTransportFCSubTask |

## DossierTransportStatusTaskCriteria

| ATE | Statut Courant | Statut Suivant | Statut Task Suivant |
| :-: | :------------: | :------------: | ------------------- |
| Oui | Init | En concertation | EnConcertationDTStatusTask |
| Non | Init | A l'étude | ALEtudeDTStatusTask |
| * | En concertation | A l'étude | ALEtudeDTStatusTask |
| * | A l'étude | A assembler | AAssemblerDTStatusTask |
| * | A assembler | A annoncer | AAnnoncerDTStatusTask |
| * | A annoncer | Conformité à saisir | ConformiteASaisirDTStatusTask |
| * | Conformité à saisir | Clos | ClosDTStatusTask |

## DossierTransportSubTasksCriteria

| Statut Courant | Statut Suivant | SubTasks |
| :------------: | :------------: | -------- |
| Init | En concertation | CreationOp1SubTask , CreationOp2SubTask , CreationOp3SubTask , CreationOp4SubTask |
| Init | A l'étude | CreationOp1SubTask , CreationOp2SubTask , CreationOp3SubTask , CreationOp4SubTask |
| A l'étude | A assembler |  |
| A assembler | A annoncer |  |
| A annoncer | Conformité à saisir |  |
| Conformité à saisir | Clos |  |

## OperationStatusTasksCriteria

| Statut Courant | Statut Suivant | Statut Task Suivant |
| :------------: | :------------: | ------------------- |
| Init | Fin | FinOpStatusTask |

## OperationSubTasksCriteria

| Type | Statut Courant | Statut Suivant | SubTasks |
| :--: | :------------: | :------------: | -------- |
| * | Init | Fin | VerifierSelonTypeOpSubTask |
| TAP | Init | Fin | VerifierSelonTypeOpSubTask => ( CreationOp21SubTask , CreationOp22SubTask , CreationOp23SubTask , CreationOp24SubTask ) |

# Code

Utilisation des tables de critères

``` java

```