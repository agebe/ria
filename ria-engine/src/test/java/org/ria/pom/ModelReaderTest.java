/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria.pom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.junit.jupiter.api.Test;

public class ModelReaderTest {

  private static final String POM = """
<?xml version="1.0" encoding="UTF-8"?>

<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>org.apache.maven</groupId>
    <artifactId>maven</artifactId>
    <version>3.8.6</version>
  </parent>

  <artifactId>maven-model</artifactId>

  <name>Maven Model</name>
  <description>Model for Maven POM (Project Object Model)</description>

  <properties>
    <checkstyle.violation.ignore>FileLength</checkstyle.violation.ignore>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.codehaus.plexus</groupId>
      <artifactId>plexus-utils</artifactId>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.modello</groupId>
        <artifactId>modello-maven-plugin</artifactId>
        <configuration>
          <version>4.0.0</version>
          <models>
            <model>src/main/mdo/maven.mdo</model>
          </models>
        </configuration>
        <executions>
          <execution>
            <id>modello</id>
            <goals>
              <goal>java</goal>
              <goal>xpp3-reader</goal>
              <goal>xpp3-extended-reader</goal>
              <goal>xpp3-writer</goal>
              <goal>xpp3-extended-writer</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-site-plugin</artifactId>
        <configuration>
          <!-- Exclude the navigation file for Maven 1 sites
               as it interferes with the site generation. -->
          <moduleExcludes>
            <xdoc>navigation.xml</xdoc>
          </moduleExcludes>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
      """;

  @Test
  public void readModelTest() throws Exception {
    MavenXpp3Reader reader = new MavenXpp3Reader();
    Model model = reader.read(new StringReader(POM));
    assertEquals("maven-model", model.getArtifactId());
    assertEquals("plexus-utils", model.getDependencies().get(0).getArtifactId());
  }

}
