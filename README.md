TaskManager

[![Build Status](https://jenkins.synaptix-labs.com/buildStatus/icon?job=Synaptix/task-manager)](https://jenkins.synaptix-labs.com/job/Synaptix/job/task-manager/)

# Principe

- Personnaliser les traitements à effectuer sur un objet métier par un simple paramétrage dans l’application.
- Les objets liés entre eux sont regroupés dans un cluster et suivis simultanément par l’ordonnanceur
- Executer automatiquement les traitements ainsi définis
- Pour l’utilisateur :
 - Visualiser les tâches effectuées pour un objet
 - Visualiser ses actions manuelles (todo)

# Dependance

```xml
<dependency>
    <groupId>com.synaptix</groupId>
    <artifactId>task-manager-engine</artifactId>
    <version>1.0.1</version>
</dependency>
```

# Faire une release

```shell
mvn versions:set -DnewVersion=1.0.1

mvn clean deploy

git add *
git commit -m "release version 1.0.1"
git tag 1.0.1
git push --follow-tags origin 1.0.1

mvn versions:set -DnewVersion=1.1.0-SNAPSHOT

```
