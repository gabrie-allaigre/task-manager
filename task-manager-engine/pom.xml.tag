<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.synaptix</groupId>
        <artifactId>task-manager</artifactId>
        <version>1.4.0</version>
    </parent>
    <artifactId>task-manager-engine</artifactId>
    <name>Task Manager Engine</name>
    <dependencies>
        <dependency>
            <groupId>com.synaptix</groupId>
            <artifactId>task-manager-shared</artifactId>
            <version>${project.version}</version>
        </dependency>

        <dependency>
            <groupId>de.jkeylockmanager</groupId>
            <artifactId>jkeylockmanager</artifactId>
        </dependency>
        <dependency>
            <groupId>com.synaptix</groupId>
            <artifactId>try</artifactId>
        </dependency>
    </dependencies>
</project>