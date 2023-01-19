package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class StringLiteralTest {

  @Test
  public void simple() {
    assertEquals("a", new Script().run("\"a\""));
  }

  @Test
  public void equals() {
    // this should work as string literals are interned
    assertTrue(new Script().evalPredicate("\"a\" == \"a\""));
  }

  @Test
  public void escapeSequence() {
    assertEquals("\n", new Script().run("\"\\n\""));
  }

  @Test
  public void print() {
    new Script().run("println(\"a\\n\")");
  }

  @Test
  public void omega() {
    assertEquals("Ω", new Script().run("\"\\u03A9\""));
  }

  @Test
  public void stringConcat() {
    assertEquals("ab", new Script().run("""
        "a"+"b"
        """));
  }

}
