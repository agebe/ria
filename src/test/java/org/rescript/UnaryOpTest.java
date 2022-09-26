package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class UnaryOpTest {

  @Test
  public void unaryPlus() {
    assertEquals(1, new Script().evalInt("+1"));
  }

  @Test
  public void unaryPlus2() {
    assertThrows(ScriptException.class, () -> new Script().run("+true"));
  }

  @Test
  public void unaryMinus() {
    assertEquals(-1, new Script().evalInt("-1"));
  }

  @Test
  public void unaryMinus2() {
    assertThrows(ScriptException.class, () -> new Script().run("-true"));
  }

}
