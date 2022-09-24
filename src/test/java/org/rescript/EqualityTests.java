package org.rescript;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EqualityTests {

  @Test
  public void equalsIntegralTrue() {
    assertTrue(new Script().evalPredicate("0==0"));
  }

  @Test
  public void equalsIntegralFalse() {
    assertFalse(new Script().evalPredicate("0==1"));
  }

  @Test
  public void notEqualsIntegralFalse() {
    assertFalse(new Script().evalPredicate("0!=0"));
  }

  @Test
  public void notEqualsIntegralTrue() {
    assertTrue(new Script().evalPredicate("0!=1"));
  }

  @Test
  public void floatPoint() {
    assertTrue(new Script().evalPredicate(".5==.5"));
  }

  @Test
  public void doubleAndFloat() {
    assertTrue(new Script().evalPredicate(".5d==.5f"));
  }

  @Test
  public void longAndInt() {
    assertTrue(new Script().evalPredicate("5l==5"));
  }

  @Test
  public void stringLiteral() {
    // String literals are interned
    assertTrue(new Script().evalPredicate("\"123\"==\"123\""));
  }

  @Test
  public void stringLiteralCase() {
    // String literals are interned
    assertFalse(new Script().evalPredicate("\"abc\"==\"ABC\""));
  }

  @Test
  public void differentObjectReference() {
    assertTrue(new Script().evalPredicate("new Object()!=new Object()"));
  }

  @Test
  public void sameObjectReference() {
    assertTrue(new Script().evalPredicate("var a = new Object();a==a;"));
  }

  @Test
  public void emptyString() {
    assertTrue(new Script().evalPredicate("\"\"==\"\""));
  }

  @Test
  public void equalsBoolTrue() {
    assertTrue(new Script().evalPredicate("true==true"));
  }

  @Test
  public void equalsBoolFalse() {
    assertFalse(new Script().evalPredicate("true==false"));
  }

  @Test
  public void notEqualsBoolTrue() {
    assertTrue(new Script().evalPredicate("true!=false"));
  }

  @Test
  public void notEqualsBoolFalse() {
    assertFalse(new Script().evalPredicate("true!=true"));
  }

  @Test
  public void invalidConversion1() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("true==1"));
  }

  @Test
  public void invalidConversion2() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("1==true"));
  }

  @Test
  public void invalidConversion3() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("new Object()==1"));
  }

  @Test
  public void invalidConversion4() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("1==new Object()"));
  }

}
