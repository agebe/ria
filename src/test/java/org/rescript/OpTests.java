package org.rescript;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OpTests {

  @Test
  public void parens() {
    Assertions.assertEquals(1, new Script().evalInt("((1))"));
  }

  @Test
  public void logicalAnd1() {
    Assertions.assertTrue(new Script().evalPredicate("true && true"));
  }

  @Test
  public void logicalAnd2() {
    Assertions.assertFalse(new Script().evalPredicate("false && true"));
  }

  @Test
  public void logicalAnd3() {
    Assertions.assertFalse(new Script().evalPredicate("true && false"));
  }

  @Test
  public void logicalAnd4() {
    Assertions.assertFalse(new Script().evalPredicate("false && false"));
  }

  @Test
  public void logicalAndErr() {
    Assertions.assertThrows(ScriptException.class, () -> new Script().evalPredicate("1 && 2"));
  }

  @Test
  public void logicalAndShort() {
    // the second expression is not evaluated, otherwise it would throw an exception as int can not be cast to bool
    Assertions.assertFalse(new Script().evalPredicate("false && 1"));
  }

  @Test
  public void logicalOr() {
    Assertions.assertTrue(new Script().evalPredicate("true || false"));
  }

  @Test
  public void logicalOrShort() {
    Assertions.assertTrue(new Script().evalPredicate("true || 2"));
  }

  @Test
  public void both() {
    Assertions.assertFalse(false || true && false);
    Assertions.assertFalse(new Script().evalPredicate("false || true && false"));
  }

  @Test
  public void arith1() {
    Assertions.assertEquals(7, new Script().evalInt("1+2*3"));
  }

}
