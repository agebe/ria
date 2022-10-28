package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class VarTest {

  @Test
  public void simpleVar() {
    Script script = new Script();
    script.run("var i;");
  }

  @Test
  public void multipleSimpleVar() {
    Script script = new Script();
    script.run("var i,j;");
  }

  @Test
  public void multipleSimpleVarFail() {
    assertThrows(ScriptException.class, () -> new Script().run("var i:j;"));
  }

  @Test
  public void assignVar() {
    Script script = new Script();
    script.run("var i = 1;");
    assertEquals(1, script.getVariable("i"));
  }

  @Test
  public void multiAssign() {
    Script script = new Script();
    script.run("var i=1, j=\"foo\";");
    assertEquals(1, script.getVariable("i"));
    assertEquals("foo", script.getVariable("j"));
  }

  @Test
  public void expressionVar() {
    Script script = new Script();
    script.run("var i = 1+1;");
    assertEquals(2, script.getVariable("i"));
  }

  @Test
  public void multipleVar() {
    Script script = new Script();
    script.run("var i=1+1, j, k=Float.MAX_VALUE;");
    assertEquals(2, script.getVariable("i"));
    assertNull(script.getVariable("j"));
    assertEquals(Float.MAX_VALUE, script.getVariable("k"));
  }

  @Test
  public void unset() {
    Script script = new Script();
    script.setVariable("v", 1);
    assertEquals("java.lang.Integer", script.runReturning("typeof v", String.class));
    script.unsetVariable("v");
    assertEquals("undefined", script.runReturning("typeof v", String.class));
  }

  @Test
  public void multiVar() {
    Script script = new Script();
    script.run("var i=1+1, j, k=Float.MAX_VALUE, (a,b,c)=42;");
  }

}
