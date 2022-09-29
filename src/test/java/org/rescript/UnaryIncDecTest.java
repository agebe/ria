package org.rescript;

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
    assertEquals(2, new Script().evalInt("var i = 1;i++;"));
  }

  @Test
  public void postDec() {
    assertEquals(0, new Script().evalInt("var i = 1;i--;"));
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
  public void testMember() {
    IncDecTest o = new IncDecTest();
    Script script = new Script();
    script.setVariable("o", o);
    script.run("o.counter++");
    assertEquals(o, script.getVariable("o"));
    assertEquals(o.counter, 1);
  }

}
