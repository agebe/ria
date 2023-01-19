package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TernaryOpTest {

  @Test
  public void ternaryTrue() {
    assertEquals(1, new Script().evalInt("true?1:2"));
  }

  @Test
  public void ternaryFalse() {
    assertEquals(2, new Script().evalInt("false?1:2"));
  }

  @Test
  public void ternaryFail() {
    assertThrows(ScriptException.class, () -> new Script().evalInt("0?1:2"));
  }

}
