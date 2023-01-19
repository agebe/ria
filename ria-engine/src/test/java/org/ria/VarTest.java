package org.ria;

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
  public void objectVar() {
    assertEquals("java.lang.Object", new Script().run("var i;typeof i;"));
  }

  @Test
  public void intVar() {
    assertEquals("int", new Script().run("int i;typeof i;"));
  }

  @Test
  public void intVar2() {
    assertEquals(0, new Script().evalInt("int i;i;"));
  }

  @Test
  public void boolVar() {
    assertEquals("boolean", new Script().run("var b = true;typeof b;"));
  }

  @Test
  public void boolVar2() {
    assertEquals("java.lang.Boolean", new Script().run("Boolean b = true;typeof b;"));
  }

  @Test
  void listVar() {
    assertEquals("java.util.List", new Script().run("List l = new java.util.ArrayList();typeof l;"));
  }

  @Test
  void listVar2() {
    assertEquals("java.util.List", new Script().run("List l = null;typeof l;"));
  }

  @Test
  void listVar3() {
    assertEquals("java.util.List", new Script().run("List l;typeof l;"));
  }

  @Test
  void listVar4() {
    assertThrows(ScriptException.class, () -> new Script().run("List l = new Object();"));
  }

  @Test
  void longVar() {
    assertEquals("long", new Script().run("long l = 1;typeof l;"));
  }

  @Test
  void longVar2() {
    assertEquals("long", new Script().run("long (l1,l2) = [1,2];typeof l2;"));
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

  @Test
  public void changeType() {
    assertEquals("long", new Script().run("long l = 1;l = 2;typeof l;"));
  }

}
