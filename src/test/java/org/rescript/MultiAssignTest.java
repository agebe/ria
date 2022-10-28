package org.rescript;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MultiAssignTest {

  @Test
  public void simple() {
    // all variables get the same value
    Script script = new Script();
    script.run("var a,b; (a,b) = 42;");
    assertEquals(42, script.getVariable("a"));
    assertEquals(42, script.getVariable("b"));
  }

  @Test
  public void simple2() {
    // all variables get the same value
    Script script = new Script();
    script.run("var (a,b) = 42;");
    assertEquals(42, script.getVariable("a"));
    assertEquals(42, script.getVariable("b"));
  }

  @Test
  public void simple3() {
    Script script = new Script();
    script.run("var (a,b) = 42, c = 43;");
    assertEquals(42, script.getVariable("a"));
    assertEquals(42, script.getVariable("b"));
    assertEquals(43, script.getVariable("c"));
  }

  @Test
  public void toMany() {
    // all variables get the same value
    Script script = new Script();
    script.run("var a,b; (a,b,c,d) = [42,43];");
    assertEquals(42, script.getVariable("a"));
    assertEquals(43, script.getVariable("b"));
    assertArrayEquals(new int[] {42,43}, (int[])script.getVariable("c"));
    assertArrayEquals(new int[] {42,43}, (int[])script.getVariable("d"));
  }

  @Test
  public void toFew() {
    // all variables get the same value
    Script script = new Script();
    script.run("var a,b; (a) = [42,43];");
    assertEquals(42, script.getVariable("a"));
  }

}
