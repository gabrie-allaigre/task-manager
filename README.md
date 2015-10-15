# TaskManager

[![build status](https://gitlab.synaptix-labs.com/ci/projects/4/status.png?ref=master)](https://gitlab.synaptix-labs.com/ci/projects/4?ref=master) [![Coverage](https://img.shields.io/sonar/https/sonar.synaptix-labs.com/com.synaptix:task-manager/overall_coverage.svg)](https://sonar.synaptix-labs.com/dashboard/index/1) [![lignes](https://img.shields.io/sonar/https/sonar.synaptix-labs.com/com.synaptix:task-manager/ncloc.svg?label=Lignes)](https://sonar.synaptix-labs.com/dashboard/index/1) [![Issue](https://img.shields.io/sonar/https/sonar.synaptix-labs.com/com.synaptix:task-manager/violations.svg)](https://sonar.synaptix-labs.com/dashboard/index/1)

## Principe

- Personnaliser les traitements à effectuer sur un objet métier par un simple paramétrage dans l’application.
- Les objets liés entre eux sont regroupés dans un cluster et suivis simultanément par l’ordonnanceur
- Executer automatiquement les traitements ainsi définis
- Pour l’utilisateur :
 - Visualiser les tâches effectuées pour un objet

## Dépendance Maven

```xml
<dependency>
    <groupId>com.synaptix</groupId>
    <artifactId>task-manager-engine</artifactId>
    <version>1.3.0</version>
</dependency>
```

## Fonctionnement

### Vocabulaire

**ITaskObject** : Objet métier manipuler par le moteur, c'est le point d'entré du moteur.

**ITaskCluster** : Créer une liaison entre plusieurs objets métiers (ITaskObject), un cluster sera archivé quand il aura plus aucune tâche des objets métier à éxécuter.

**IStatusTask** : Tâche de monté de statut.

**ISubTask** : Sous tâche, elle est entre 2 tâches de monté de statut.

**IStatusGraph** : Graphe des tâches de monté de statut, à partir d'un statut courant, donne les statuts suivant.

## Exemple simple

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

## Faire une release

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
