package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

// removed char literals from the language as single quote enclosed strings should behave the same as
// double quote enclosed strings. char can be created via casting from String
public class CharLiteralTest {

  @Test
  public void simple() {
    assertEquals('a', new Script().run("(char)'a'"));
  }

  @Test
  public void inc() {
    assertEquals('b', new Script().run("char c = 'a';c++;c;"));
  }

  @Test
  public void dec() {
    assertEquals('b', new Script().run("char c = 'c';c--;c;"));
  }

  @Test
  public void preInc() {
    assertEquals('b', new Script().run("char c = 'a';++c;"));
  }

  @Test
  public void preDec() {
    assertEquals('b', new Script().run("char c = 'c';--c;"));
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
    assertEquals(66, new Script().evalInt("(char)'A' + 1"));
  }

  @Test
  public void sub() {
    assertEquals(65, new Script().evalInt("(char)'B' - 1"));
  }

  @Test
  public void mul() {
    assertEquals(65*2, new Script().evalInt("(char)'A' * 2"));
  }

  @Test
  public void div() {
    assertEquals(65/2, new Script().evalInt("(char)'A' / 2"));
  }

  @Test
  public void mod() {
    assertEquals(65%2, new Script().evalInt("(char)'A' % 2"));
  }

  @Test
  public void ge() {
    assertTrue(new Script().evalPredicate("(char)'B' >= (char)'A'"));
  }

  @Test
  public void lt() {
    assertTrue(new Script().evalPredicate("(char)'A' < (char)'B'"));
  }

  @Test
  public void ltFalse() {
    assertFalse(new Script().evalPredicate("(char)'A' < (char)'A'"));
  }

  @Test
  public void escapeSequence() {
    assertEquals(10, new Script().evalInt("(char)'\\n'"));
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
  public void unaryPlus() {
    assertEquals(122, new Script().run("+(char)'z'"));
  }

  @Test
  public void unaryMinus() {
    assertEquals(-122, new Script().run("-(char)'z'"));
  }

  @Test
  public void stringConcat() {
    assertEquals("ab", new Script().run("""
        "a"+'b'
        """));
  }

  @Test
  public void castToString() {
    assertEquals("a", new Script().run("""
        String s = 'a';
        s;
        """));
  }

  @Test
  public void invalidCharCast() {
    assertThrows(ScriptException.class, () -> new Script().run("(char)'ab'"));
  }

}
