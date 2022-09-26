package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test
  public void unaryLogicalNot() {
    assertTrue(new Script().evalPredicate("!false"));
  }

  @Test
  public void unaryLogicalNot2() {
    // operator precedence
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("!1>2)"));
  }

  @Test
  public void unaryLogicalNot3() {
    assertTrue(new Script().evalPredicate("!(1>2)"));
  }

  @Test
  public void unaryLogicalNot4() {
    assertFalse(new Script().evalPredicate("!true"));
  }

}
