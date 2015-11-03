TaskManager
===========

[![build status](https://gitlab.synaptix-labs.com/ci/projects/4/status.png?ref=master)](https://gitlab.synaptix-labs.com/ci/projects/4?ref=master) [![Coverage](https://img.shields.io/sonar/https/sonar.synaptix-labs.com/com.synaptix:task-manager/overall_coverage.svg)](https://sonar.synaptix-labs.com/dashboard/index/1) [![lignes](https://img.shields.io/sonar/https/sonar.synaptix-labs.com/com.synaptix:task-manager/ncloc.svg?label=Lignes)](https://sonar.synaptix-labs.com/dashboard/index/1) [![Issue](https://img.shields.io/sonar/https/sonar.synaptix-labs.com/com.synaptix:task-manager/violations.svg)](https://sonar.synaptix-labs.com/dashboard/index/1)

# Principe

- Personnaliser les traitements à effectuer sur un objet métier par un simple paramétrage dans l’application.
- Les objets liés entre eux sont regroupés dans un cluster et suivis simultanément par l’ordonnanceur
- Executer automatiquement les traitements ainsi définis

Il y a 2 types de tâches :

- Tâche de statut : Tâche principal qui gère la monté de statut de l'objet métier, dans le cas d'une branche `A->(B,C)`, qu'une seule tâche ne peut passé, soit B ou C, l'autre sera surprimer. Les boucles sont autorisées etre les statuts.
- Sous-tâche : Tâche se trouvant entre 2 tâches de statut, toutes les sous-tâches doivent être éxécuté pour que la tâche de statut soit éxécuté.

Les objets métiers peuvent être regroupé dans un **Cluster**, cela permet de liéer les objets et leurs cycles de vies ensemble.

# Dépendance Maven

```xml
<dependency>
    <groupId>com.synaptix</groupId>
    <artifactId>task-manager-engine</artifactId>
    <version>1.3.0</version>
</dependency>
```

# Exemple simple

```java
TaskManagerEngine engine = new TaskManagerEngine(
    // Configuration
    TaskManagerConfigurationBuilder.newBuilder()
        // Répertoire des "object manager", référence la manipulation des ITaskObject
        .taskObjectManagerRegistry(
            TaskObjectManagerRegistryBuilder.newBuilder()
                // Ajout d'un "object manager" pour l'objet "BusinessObject", le statut sera de type "String"
                .addTaskObjectManager(
                    TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(
                        // Ajout d'un status graph, de null -> A
                        StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build()
                    ).build()
                ).build())
        // Répertoire des services de tâches
        .taskDefinitionRegistry(
            TaskDefinitionRegistryBuilder.newBuilder()
                // Ajout d'un service de tâche de code "ATask"
                .addTaskDefinition(
                    TaskDefinitionBuilder.newBuilder("ATask", new MultiUpdateStatusTaskService("A")).build()
                ).build())
    .build()
);

// Objet métier
BusinessObject businessObject = new BusinessObject();
// Execution du moteur
engine.startEngine(businessObject);
```

# Fonctionnement

## Vocabulaire

**ITaskObject** : Objet métier manipuler par le moteur, c'est le point d'entré du moteur.

**ITaskCluster** : Créer une liaison entre plusieurs objets métiers (ITaskObject), un cluster sera archivé quand il aura plus aucune tâche des objets métier à éxécuter.

**IStatusTask** : Tâche de monté de statut.

**ISubTask** : Sous tâche, elle est entre 2 tâches de monté de statut.

**IStatusGraph** : Graphe des tâches de monté de statut, à partir d'un statut courant, donne les statuts suivant.

**ITaskObjectManager** : Gestion du TaskObject, création du graphe des status et du sous-graphe entre deux statuts

**ITaskDefinition** : Définition d'une tâche service

**ITaskService** : Implémentation d'une tâche service

**ITaskCycleListener** : Ecouteur sur le cycle de vie d'une tâche

**ITaskManagerConfiguration** : Configuration du moteur, TaskObjectManager, Reader, Writer, etc

**ITaskFactory** : Créer les tâches

**ITaskManagerReader** : Lecture des tâches

**ITaskManagerWriter** : Ecriture des tâches

**TaskManagerEngine** : Moteur de l'ordonnanceur de tâche

## TaskObjectManager

Gestion d'un TaskObject. 2 méthodes pour les créer, soit simple soit personalisé

### Par défaut

Utilisation de TaskObjectManagerBuilder, le graphe est définie grâce à StatusGraphBuilder, et entre chaque statut il est possible de décrire des sous-tâches.

``` java
TaskObjectManagerBuilder.<String, BusinessObject>newBuilder(BusinessObject.class).statusGraphs(
                        // Ajout d'un status graph, de null -> A
                        StatusGraphsBuilder.<String>newBuilder().addNextStatusGraph("A", "ATask").build()
                    ).addTaskChainCriteria(null,"A","TASK1=>TASK2").build()
```

### Personalisé

Implémentation de ITaskObjectManager

``` java
public interface ITaskObjectManager<E, F extends ITaskObject>
```

La class de l'objet task que représente le manager

``` java
Class<F> getTaskObjectClass();
```

Utiliser à l'initialisation du graphe sur l'objet, il donne le premier statut de l'objet courant

``` java
E getInitialStatus(F taskObject);
```

A partir d'un statut courant, renvoie la liste des statuts suivant

``` java
List<IStatusGraph<E>> getNextStatusGraphsByTaskObjectType(IStatusTask statusTask, E currentStatus);
```

A partir de deux statuts, renvoie la chaine de sous tâches

``` java
String getTaskChainCriteria(IStatusTask statusTask, E currentStatus, E nextStatus);
```

### Registre

Par la suite chaque `TaskObjectManager` est mis dans un registre `ITaskObjectManagerRegistry`. Dans la plus part des cas, le constructeur suffira `TaskObjectManagerRegistryBuilder`.

## ITaskDefinition

Définition des `ITaskService`, il contient par défaut un code et un service. L'objet est transmit au appels de services et aux Listeners.

L'idée est d'avoir une seule implémentation d'un `ITaskService` et de pouvoir les parametrer avec la définition. L'objet peut être persisté.

### Par défaut

L'implémentation par défaut, il faut utiliser `TaskDefinitionBuilder`.

### Registre

Les définitions sont mis dans un registre `ITaskDefinitionRegistry`, il existe une implémentation par défaut `TaskDefinitionRegistryBuilder`. Une implémentation spécifique peut être fait dans le cas d'une persistance en base de donnée de la liste des définitions.

## ITaskFactory

Créer les tâches pour le moteur.

``` java
public interface ITaskFactory
```

Création d'un cluster, par défaut les variables doivent renvoyer `false`. 

``` java
ITaskCluster newTaskCluster();
```

Création d'une sous-tâche.

``` java
ISubTask newSubTask(String codeSubTaskDefinition);
```

Est-ce que la tâche est une sous-tâche ?

``` java
boolean isSubTask(ICommonTask task);
```

Création d'une tâche statut.

``` java
IStatusTask newStatusTask(String codeStatusTaskDefinition, Class<? extends ITaskObject> taskObjectClass, Object currentStatus);
```

Est-ce que la tâche est une tâche statut.

``` java
boolean isStatusTask(ICommonTask task);
```

## ITaskManagerReader

Permet au moteur de lire les tâches et de connaitres l'avancement. Il est à implementer selon la persistance. L'implémentation dépent de `ITaskFactory`

``` java
public interface ITaskManagerReader
```

Renvoie le Cluster à partir d'un TaskObject

``` java
ITaskCluster findTaskClusterByTaskObject(ITaskObject taskObject);
```

Renvoie la liste des objets métiers liéé à un Cluster.

``` java
List<? extends ITaskObject> findTaskObjectsByTaskCluster(ITaskCluster taskCluster);
```

Renvoie la liste des tâches courantes, soit des tâches de statut ou des sous-tâches.

``` java
List<? extends ICommonTask> findCurrentTasksByTaskCluster(ITaskCluster taskCluster);
```

Renvoie la liste des tâches suivantes, soit des tâches de statut ou des sous-tâches, par rapport à une sous-tâche. Attention ne renvoie que les tâches possibles.

``` java
List<? extends ICommonTask> findNextTasksBySubTask(ISubTask subTask);
```

Renvoie la liste des tâches de début d'une autre branche de statut, soit des tâches de statut ou des sous-tâches. Quand une tâche de statut réussie, les autres tâches de statut sont supprimées. La tâche de statut en paramètre est celle qui a reussie.

``` java
List<? extends ICommonTask> findOtherBranchFirstTasksByStatusTask(IStatusTask statusTask);
```

## ITaskManagerWriter

Permet au moteur d'ecrire les nouvelles tâches et l'avancement des tâches.

``` java
public interface ITaskManagerWriter
```

Sauvegarde un nouveau Cluster.

``` java
ITaskCluster saveNewTaskCluster(ITaskCluster taskCluster);
```

Sauvegarde des nouveaux graphes pour des objets métiers et les rattaches au Cluster. Chaque graphe contient une première tâche de statut.

``` java
ITaskCluster saveNewGraphFromTaskCluster(ITaskCluster taskCluster, List<Pair<ITaskObject, IStatusTask>> taskObjectTasks);
```

Sauvegarde le déttachement d'objet métier à un Cluster.

``` java
void saveRemoveTaskObjectsFromTaskCluster(ITaskCluster taskCluster, List<ITaskObject> taskObjects);
```

Sauvegarde le déplacement des objets métiers dans un autres Cluster.

``` java
ITaskCluster saveMoveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, Map<ITaskCluster, List<ITaskObject>> modifyClusterMap);
```

Archive un Cluster, la méthode `isCheckGraphCreated` doit être modifiée.

``` java
ITaskCluster archiveTaskCluster(ITaskCluster taskCluster);
```

Sauvegarde la réussite d'une tâche `toDoneTask` et actives les tâches suivantes.

``` java
void saveNextTasksInTaskCluster(ITaskCluster taskCluster, ICommonTask toDoneTask, Object taskServiceResult, List<ICommonTask> nextCurrentTasks);
```

Sauvegarde la réussite d'une tâche de statut `toDoneTask`, la liste des nouvelles tâches, le lien entre les tâches, les liens entre les autres branches, les nouvelles tâches courante et les tâches à supprimer sont fournit et à sauvegarder.

``` java
void saveNewNextTasksInTaskCluster(ITaskCluster taskCluster, IStatusTask toDoneTask, Object taskServiceResult, List<ICommonTask> newTasks, Map<ISubTask, List<ICommonTask>> linkNextTasksMap,
            Map<IStatusTask, List<ICommonTask>> otherBranchFirstTasksMap, List<ICommonTask> nextCurrentTasks, List<ICommonTask> deleteTasks);
```

Sauvegarde une tâche qui a échoué, son résultat et une erreur peuvent être fournit.

``` java
void saveNothingTask(ITaskCluster taskCluster, ICommonTask nothingTask, Object taskServiceResult, Throwable errorMessage);
```

## TaskManagerEngine

Moteur du Task Manager, il fournit différentes fonctions pour faire créer, avancer, attacher et détacher un objet métier à un Cluster.

Démarre le moteur sur des objets métiers, soit un Cluster sera crée (si tous lers objets sont sans Cluster ils seront tous attaché au même), soit le Cluster existe déjà est fait avancé les tâches possibles.

``` java
public ITaskCluster startEngine(ITaskObject... taskObjects);
```

Attache des objets métiers à un Cluster et lance le moteur.

``` java
public void addTaskObjectsToTaskCluster(ITaskCluster taskCluster, ITaskObject... taskObjects);
```

Déttache des objets métiers de leur Cluster et lance le moteur.

``` java
public void removeTaskObjectsFromTaskCluster(ITaskObject... taskObjects);
```

Déplace les objets métiers dans un nouveau Cluster ou dans un Cluster existant puis lance le moteur. 

``` java
public ITaskCluster moveTaskObjectsToNewTaskCluster(ITaskObject... taskObjects);

public void moveTaskObjectsToTaskCluster(ITaskCluster dstTaskCluster, ITaskObject... taskObjects);
```

## ITaskService

Coeur d'une tâche, code exécuté de la tâche.

Méthode lancé par le moteur pour chaque tâche, celui-ci fournit un contexte et la tâche en cours. Elle doit retourné le résultat de l'éxécution.

``` java
IExecutionResult execute(IEngineContext context, ICommonTask commonTask);
```

Le contexte contient différents méthodes :

- Le Cluster et la ITaskDefinition courante
- Des méthodes pour attacher, déplacer et déttaché des objets métiers à un Cluster. Les actions seront faites que si la tâche réussie. Et le moteur sera relancé pour les Cluster ajoutés ou modifiés.

Le resultat renvoie différents informations :

| Méthode | Commentaire |
| ------- | ----------- |
| `boolean isFinished();` | La tâche est-elle terminé ? | 
| `boolean isNoChanges();` | La tâche n'a modifié aucune donnée, utilisé pour accélérer le moteur | 
| `Object getResult();` | Le resultat de la tâche | 
| `boolean mustStopAndRestartTaskManager();` | Le moteur devra être relancé après l'éxécution de la tâche | 

Une implémentation du resultat est `ExecutionResultBuilder`.

# Faire une release

```bash
mvn versions:set -DnewVersion=1.0.1

mvn clean deploy

git add *
git commit -m "release version 1.0.1"
git tag 1.0.1
git push --follow-tags origin 1.0.1

mvn versions:set -DnewVersion=1.1.0-SNAPSHOT
mvn versions:commit

git add *
git commit -m "new version 1.1.0-SNAPSHOT"
git push origin master

```