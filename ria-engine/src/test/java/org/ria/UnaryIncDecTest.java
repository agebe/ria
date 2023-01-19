package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class UnaryIncDecTest {

  public static int COUNTER = 0;

  public static class IncDecTest {
    public int counter;
  }

  @Test
  public void postInc() {
    assertEquals(1, new Script().evalInt("var i = 1;i++;"));
  }

  @Test
  public void postIncDouble() {
    assertEquals(1.5, new Script().evalDouble("var i = 1.5d;i++;"));
  }


  @Test
  public void postDec() {
    assertEquals(1, new Script().evalInt("var i = 1;i--;"));
  }

  @Test
  public void postIncFail() {
    assertThrows(ScriptException.class, () ->new Script().evalInt("0++"));
  }

  @Test
  public void testCounter() {
    new Script().run("%s.COUNTER++".formatted(this.getClass().getName()));
    assertEquals(1, COUNTER);
  }

  @Test
  public void testMemberPost() {
    IncDecTest o = new IncDecTest();
    Script script = new Script();
    script.setVariable("o", o);
    script.run("org.junit.jupiter.api.Assertions.assertEquals(0, o.counter++)");
    assertEquals(o, script.getVariable("o"));
    assertEquals(1, o.counter);
    script.run("org.junit.jupiter.api.Assertions.assertEquals(1, o.counter--)");
    assertEquals(0, o.counter);
  }

  @Test
  public void preInc() {
    assertEquals(2, new Script().evalInt("var i = 1;++i;"));
  }

  @Test
  public void preIncDouble() {
    assertEquals(2.5, new Script().evalDouble("var i = 1.5d;++i;"));
  }


  @Test
  public void preDec() {
    assertEquals(0, new Script().evalInt("var i = 1;--i;"));
  }

  @Test
  public void preIncFail() {
    assertThrows(ScriptException.class, () ->new Script().evalInt("++0"));
  }

  @Test
  public void preIncFail2() {
    assertThrows(ScriptException.class, () ->new Script().evalInt("++\"123\""));
  }


  @Test
  public void testMemberPre() {
    IncDecTest o = new IncDecTest();
    Script script = new Script();
    script.setVariable("o", o);
    script.run("org.junit.jupiter.api.Assertions.assertEquals(1, ++o.counter)");
    assertEquals(o, script.getVariable("o"));
    assertEquals(1, o.counter);
    script.run("org.junit.jupiter.api.Assertions.assertEquals(0, --o.counter)");
    assertEquals(0, o.counter);
  }

}
