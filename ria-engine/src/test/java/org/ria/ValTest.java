package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ValTest {

  @Test
  public void simpleVal() {
    new Script().run("val i;");
  }

  @Test
  public void longVal() {
    assertEquals("long", new Script().run("val i = 5l;typeof i;"));
  }

  @Test
  public void cantChange() {
    assertThrows(ScriptException.class, () -> new Script().run("val i = 42;i++;"));
  }

  @Test
  public void cantChange2() {
    assertThrows(ScriptException.class, () -> new Script().run("val i = 42;i=1;"));
  }

  @Test
  public void init1() {
    assertEquals(1, new Script().run("val i;i=1;"));
  }

  @Test
  public void init2() {
    assertThrows(ScriptException.class, () -> new Script().run("val i;i=1;i=2;"));
  }

}
