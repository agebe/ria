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

  @Test
  public void arith2() {
    Assertions.assertEquals(8.5, new Script().evalFloat("1+2.5*3"), 0.01);
  }

  @Test
  public void divOp() {
    Assertions.assertEquals(5, new Script().evalInt("10/2"));
  }

  @Test
  public void modOp() {
    Assertions.assertEquals(1, new Script().evalInt("10%3"));
  }

  @Test
  public void subOp() {
    Assertions.assertEquals(9, new Script().evalDouble("10d-1"), 0.01);
  }

  @Test
  public void subOp2() {
    Assertions.assertEquals(0, new Script().evalDouble("Double.MAX_VALUE - Double.MAX_VALUE"), 0.01);
  }

  @Test
  public void concat() {
    System.out.println(null+"foo");
    System.out.println("foo"+null);
    Assertions.assertEquals("foo0", new Script().runReturning("\"foo\" + 0", String.class));
    Assertions.assertEquals("11.0s1 s21", new Script().runReturning("5.5 * 2 + \"s1 \" + \"s2\" + 1", String.class));
    Assertions.assertEquals("nulls1", new Script().runReturning("null + \"s1\"", String.class));
    Assertions.assertEquals("s1null", new Script().runReturning("\"s1\" + null", String.class));
  }

}
