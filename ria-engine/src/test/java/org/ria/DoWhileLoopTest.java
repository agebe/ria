package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DoWhileLoopTest {

  @Test
  public void simple() {
    assertEquals(2, new Script().evalInt("1;do 2; while(false);"));
  }

  @Test
  public void simple2() {
    assertEquals(2, new Script().evalInt("1;do {2;} while(false);"));
  }

  @Test
  public void withBreak() {
    assertEquals(1, new Script().evalInt("1;do {break;2;} while(false);"));
  }

  @Test
  public void withContinue() {
    assertEquals(1, new Script().evalInt("1;do {continue;2;} while(false);"));
  }

}
