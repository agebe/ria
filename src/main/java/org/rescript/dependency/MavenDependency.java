package org.rescript.dependency;

import java.util.List;

import org.rescript.ScriptException;

public class MavenDependency implements Dependency {

  @Override
  public List<DependencyNode> resolve() {
    // FIXME
    // support dependencies like below (with or without enclosing <dependencies> tag)
    /*
<dependency>
  <groupId>io.github.agebe</groupId>
  <artifactId>kvd-client</artifactId>
  <version>0.6.2</version>
</dependency>
<dependency>
  <groupId>org.hamcrest</groupId>
  <artifactId>hamcrest</artifactId>
  <version>123</version>
</dependency>

or 

    <dependencies>
        <dependency>
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest</artifactId>
            <version>123</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>123</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.awaitility</groupId>
            <artifactId>awaitility-test-support</artifactId>
            <version>123</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

     */
    throw new ScriptException("not implemented yet");
  }

}
