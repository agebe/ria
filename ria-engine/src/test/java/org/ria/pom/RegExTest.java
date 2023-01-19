package org.ria.pom;

import org.junit.jupiter.api.Test;

public class RegExTest {

  @Test
  public void regex() {
    System.out.println("https://repo.maven.apache.org/maven2/".replaceAll("\\W", "_"));
    
  }

}
