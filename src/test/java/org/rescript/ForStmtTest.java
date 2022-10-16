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
    assertEquals(3, new Script().evalInt("for(var i=0;i<3;i++) println(i);i;"));
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
    assertEquals("undefined", new Script().runReturning("for(var i=0;i<3;i++);typeof i;", String.class));
  }

}
