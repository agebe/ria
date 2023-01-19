package org.ria;

import org.junit.jupiter.api.Test;

public class DependencyTest {

  @Test
  public void simple() {
    new Script("""
        repositories {
          mavenCentral()
        }
        dependencies {
          '''
          <dependency>
            <groupId>io.github.agebe</groupId>
            <artifactId>kvd-client</artifactId>
            <version>0.6.2</version>
            <exclusions>
              <exclusion>
                <groupId>org.slf4j</groupId>
                <artifactId>slf4j-api</artifactId>
              </exclusion>
            </exclusions>
          </dependency>
          <!-- commons lang not really required, just testing pom import -->
          <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
          </dependency>
          '''
          //'io.github.agebe:kvd-client:0.6.2'
        }
        import kvd.client.KvdClientBuilder;
        var builder = new KvdClientBuilder();
        println(typeof builder);
        println(builder);
        """).run();
  }

}
