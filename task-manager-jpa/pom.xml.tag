<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>task-manager</artifactId>
        <groupId>com.synaptix</groupId>
        <version>1.4.0</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>task-manager-jpa</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.synaptix</groupId>
            <artifactId>task-manager-engine</artifactId>
            <version>${project.version}</version>
        </dependency>

        <dependency>
            <groupId>org.hibernate.javax.persistence</groupId>
            <artifactId>hibernate-jpa-2.1-api</artifactId>
        </dependency>

        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.apache.derby</groupId>
            <artifactId>derby</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>