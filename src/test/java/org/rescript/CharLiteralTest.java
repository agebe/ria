package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CharLiteralTest {

  @Test
  public void simple() {
    assertEquals('a', new Script().run("'a'"));
  }

  @Test
  public void inc() {
    assertEquals('b', new Script().run("var c = 'a';c++;c;"));
  }

  @Test
  public void dec() {
    assertEquals('b', new Script().run("var c = 'c';c--;c;"));
  }

  @Test
  public void preInc() {
    assertEquals('b', new Script().run("var c = 'a';++c;"));
  }

  @Test
  public void preDec() {
    assertEquals('b', new Script().run("var c = 'c';--c;"));
  }

  @Test
  public void equals() {
    assertTrue(new Script().evalPredicate("'a' == 'a'"));
  }

  @Test
  public void equals2() {
    assertFalse(new Script().evalPredicate("'a' == 'b'"));
  }

  @Test
  public void add() {
    assertEquals(66, new Script().evalInt("'A' + 1"));
  }

  @Test
  public void escapeSequence() {
    assertEquals(10, new Script().evalInt("'\\n'"));
  }

  @Test
  public void print() {
    new Script().run("println('\\u03A9')");
  }

  @Test
  public void omega() {
    assertEquals('Ω', new Script().evalChar("'\\u03A9'"));
  }

  @Test
  public void evalChar() {
    assertEquals('A', new Script().evalChar("'A'"));
  }

  @Test
  public void stringConcat() {
    assertEquals("ab", new Script().run("""
        "a"+'b'
        """));
  }

  @Test
  public void invalidCharLiteral() {
    assertThrows(ScriptException.class, () -> new Script().run("'ab'"));
  }

}
