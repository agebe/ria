package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ReturnStatementTest {

  @Test
  public void simple() {
    assertEquals(42, new Script().run("function f1() {return 42;} f1();"));
  }

}
