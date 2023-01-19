package org.ria;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RelationalTests {

  @Test
  public void ge() {
    assertTrue(new Script().evalPredicate("0>=0"));
  }

  @Test
  public void le() {
    assertTrue(new Script().evalPredicate("0<=0"));
  }

  @Test
  public void gt() {
    assertFalse(new Script().evalPredicate("0>0"));
  }

  @Test
  public void lt() {
    assertFalse(new Script().evalPredicate("0<0"));
  }

  @Test
  public void geFloat() {
    assertTrue(new Script().evalPredicate("0.2>=0.2"));
  }

  @Test
  public void leFloat() {
    assertTrue(new Script().evalPredicate("0.1<=0.2"));
  }

  @Test
  public void gtFloat() {
    assertFalse(new Script().evalPredicate(".3>.4"));
  }

  @Test
  public void ltFloat() {
    assertFalse(new Script().evalPredicate("-1.1<-2.5"));
  }

  @Test
  public void ltLong() {
    assertFalse(new Script().evalPredicate("-1l<-2l"));
  }

  @Test
  public void invalidConversion1() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("true>=1"));
  }

  @Test
  public void invalidConversion2() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("1<=true"));
  }

  @Test
  public void invalidConversion3() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("new Object()>1"));
  }

  @Test
  public void invalidConversion4() {
    assertThrows(ScriptException.class, () -> new Script().evalPredicate("1<new Object()"));
  }

}
