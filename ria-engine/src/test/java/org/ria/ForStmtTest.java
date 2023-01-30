/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria;

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
    assertEquals(-3, script.getVariable("b"));
  }

  @Test
  public void forArray() {
    Script script = new Script();
    assertEquals(44, script.evalInt("""
        var a = arrayof [42,43,44];
        for(var i=0;i<a.length;i++) {
          a[i];
        }
        """));
  }

  @Test
  public void forMultiAssign3() {
    Script script = new Script();
    assertEquals(3, script.evalInt("long a,b;for((a,b)=0;a<3;a++,b--) {}; a;"));
    assertEquals(-3l, script.getVariable("b"));
    assertEquals("long", script.run("typeof b;"));
  }

  @Test
  public void forWithType() {
    Script script = new Script();
    assertEquals("long", script.run("for(long a=0;a<3;a++) {typeof a;};"));
  }

}
