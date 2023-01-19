package org.ria;

import org.junit.jupiter.api.Test;

public class ScopeTest {

  @Test
  public void scope1() {
    new Script().run("""
        import static org.junit.jupiter.api.Assertions.assertEquals;
        var global = 1;
        {
          var local = 2;
          assertEquals("int", typeof local);
        }
        assertEquals("int", typeof global);
        assertEquals("undefined", typeof local);
        """);
  }

}
