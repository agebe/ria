package org.rescript;

import org.junit.jupiter.api.Test;

public class TypesTest {

  @Test
  public void string() {
    new Script().run("""
        var l = List.of("1,2", "2", 'foo');
        println(l.get(0).split(",")[0]);
        """);
  }

}
