<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.synaptix</groupId>
        <artifactId>task-manager</artifactId>
        <version>1.4.0</version>
    </parent>
    <artifactId>task-manager-example</artifactId>
    <name>Task Manager Example</name>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <compilerArgument>-proc:none</compilerArgument>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.bsc.maven</groupId>
                <artifactId>maven-processor-plugin</artifactId>
                <executions>
                    <execution>
                        <id>process</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>process</goal>
                        </goals>
                        <configuration>
                            <processors>
                                <processor>com.synaptix.component.annotation.processor.SynaptixComponentProcessor</processor>
                                <processor>org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor</processor>
                            </processors>
                        </configuration>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>com.synaptix</groupId>
                        <artifactId>SynaptixComponent</artifactId>
                        <version>${synaptixlibs.version}</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
    <dependencies>
        <dependency>
            <groupId>com.synaptix</groupId>
            <artifactId>SynaptixEntity</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>javax.persistence</groupId>
                    <artifactId>persistence-api</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>com.synaptix</groupId>
            <artifactId>task-manager-engine</artifactId>
            <version>${project.version}</version>
        </dependency>

        <dependency>
            <groupId>com.synaptix</groupId>
            <artifactId>task-manager-component</artifactId>
            <version>${project.version}</version>
        </dependency>

        <dependency>
            <groupId>com.synaptix</groupId>
            <artifactId>task-manager-jpa</artifactId>
            <version>${project.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.derby</groupId>
            <artifactId>derby</artifactId>
        </dependency>

        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
        </dependency>

        <dependency>
            <groupId>commons-beanutils</groupId>
            <artifactId>commons-beanutils</artifactId>
            <version>1.9.2</version>
        </dependency>

        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-jpamodelgen</artifactId>
        </dependency>
    </dependencies>
</project>