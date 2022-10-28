package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ForStmtTest {

  @Test
  public void forStmt() {
    assertEquals(1, new Script().evalInt("for(;;) return 1;"));
  }

  @Test
  public void forStmt2() {
    assertEquals(3, new Script().evalInt("var result = 0;for(var i=0;i<3;i++) result = i;result;"));
  }

  @Test
  public void forMultiVarStmt() {
    assertEquals(8, new Script().evalInt("var result;for(var i=0,j=1;i<3;i++,j=j*2) result=j;"));
  }

  @Test
  public void forStmt3() {
    assertEquals(3, new Script().evalInt("var i;for(i=0;i<3;i++) println(i);i;"));
  }

  @Test
  public void forMultiAssign() {
    assertEquals(4, new Script().evalInt("var i,j;for(i=0,j=1;i<3;i++,j++) println(j);j;"));
  }

  @Test
  public void forScope() {
    assertEquals("undefined", new Script().run("for(var i=0;i<3;i++);typeof i;"));
  }

  @Test
  public void forBreak() {
    assertEquals(0, new Script().evalInt("var i;for(i=0;i<3;i++) break;i;"));
  }

  @Test
  public void forBreak2() {
    assertEquals(0, new Script().evalInt("var i;for(i=0;i<3;i++) {break;} i;"));
  }

  @Test
  public void forBreak3() {
    assertEquals(42, new Script().evalInt("var a = 42;for(var i=0;i<3;i++) {break;a=1;} a;"));
  }

  @Test
  public void forContinue() {
    assertEquals(42, new Script().evalInt("var a=42;for(var i=0;i<3;i++) {continue;a=1;}; a;"));
  }

  @Test
  public void forMultiAssign2() {
    Script script = new Script();
    assertEquals(3, script.evalInt("var a,b;for((a,b)=0;a<3;a++,b--) {}; a;"));
    assertEquals(-3, new Script().getVariable("b"));
  }

}
