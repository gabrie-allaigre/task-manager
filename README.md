# TaskManager

[![build status](https://gitlab.synaptix-labs.com/ci/projects/4/status.png?ref=master)](https://gitlab.synaptix-labs.com/ci/projects/4?ref=master) [![Coverage](https://img.shields.io/sonar/https/sonar.synaptix-labs.com/com.synaptix:task-manager/overall_coverage.svg)](https://sonar.synaptix-labs.com/dashboard/index/1) [![lignes](https://img.shields.io/sonar/https/sonar.synaptix-labs.com/com.synaptix:task-manager/ncloc.svg?label=Lignes)](https://sonar.synaptix-labs.com/dashboard/index/1) [![Issue](https://img.shields.io/sonar/https/sonar.synaptix-labs.com/com.synaptix:task-manager/violations.svg)](https://sonar.synaptix-labs.com/dashboard/index/1)

## Principe

- Personnaliser les traitements à effectuer sur un objet métier par un simple paramétrage dans l’application.
- Les objets liés entre eux sont regroupés dans un cluster et suivis simultanément par l’ordonnanceur
- Executer automatiquement les traitements ainsi définis
- Pour l’utilisateur :
 - Visualiser les tâches effectuées pour un objet
 - Visualiser ses actions manuelles (todo)

## Usage

```xml
<dependency>
    <groupId>com.synaptix</groupId>
    <artifactId>task-manager-engine</artifactId>
    <version>1.2.0</version>
</dependency>
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
