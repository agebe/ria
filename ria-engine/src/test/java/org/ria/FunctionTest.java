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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FunctionTest {

  @Test
  public void simple() {
    assertNull(new Script().run("function f1() {}"));
  }

  @Test
  public void simpleWithCall() {
    assertNull(new Script().run("function f1() {} f1();"));
  }

  @Test
  public void simple2() {
    assertNull(new Script().run("function f1() {42;}"));
  }

  @Test
  public void simple2WithCall() {
    assertEquals(42, new Script().run("function f1() {42;} f1();"));
  }

  @Test
  public void withParams() {
    assertEquals(4, new Script().run("function f1(a) {a*2;} f1(2);"));
  }

  @Test
  public void withMultiParams() {
    assertEquals(8.0, new Script().run("function f1(a,b) {Math.pow(a,b);} f1(2d,3d);"));
  }

  @Test
  public void withMultiFunctions() {
    assertEquals("f2", new Script().run("function f1(a,b,c) {c;} function f2(a) {a;} f2(\"f2\");"));
  }

  @Test
  public void nested() {
    assertEquals(2, new Script().run("""
        function f1(a) {
          function f2(b) {
            a+b;
          }
          f2(a);
        }
        f1(1);
        """));
  }

  @Test
  public void recusive() {
    assertEquals(120, new Script().run("""
        function fac(num) {
          num==0?1:num*fac(num-1);
        }
        fac(5);
        """));
  }

  @Test
  public void withReturnStatement() {
    assertEquals(42, new Script().run("function f1() {return 42; 1;} f1();"));
  }

  @Test
  public void withLoopBreak() {
    assertEquals(1, new Script().run("""
        function f1(a) {
          var current;
          for(var i=0;i<a;i++) {
            current = i;
            if(i == 1) break;
          }
          return current;
        }
        f1(3);
        """));
  }

  @Test
  public void withLoopBreakIllegal() {
    assertThrows(ScriptException.class, () -> new Script().run("""
        function f1(a) {
          break;
        }
        var current;
        for(var i=0;i<a;i++) {
          current = i;
          f1(i);
        }
        return current;
        """));
  }

  @Test
  public void withLoopContinue() {
    assertEquals(42, new Script().run("""
        function f1(a) {
          var current = 42;
          for(var i=0;i<a;i++) {
            continue;
            current = i;
          }
          return current;
        }
        f1(3);
        """));
  }

  @Test
  public void withLoopContinueIllegal() {
    assertThrows(ScriptException.class, () -> new Script().run("""
        function f1(a) {
          continue;
        }
        var current = 42;
        for(var i=0;i<a;i++) {
          f1(i);
          current = i;
        }
        return current;
        """));
  }

  @Test
  public void siblingFunction() {
    assertEquals(42, new Script().run("""
        function f1(a) {
          return a;
        }
        function f2(a) {
          return f1(a);
        }
        f2(42);
        """));
  }

  @Test
  public void parentFunction() {
    assertEquals(0, new Script().run("""
        function f1(a) {
          function f2(b) {
            f1(b-1);
          }
          return a>0?f2(a):a;
        }
        f1(42);
        """));
  }

  @Test
  public void nested2() {
    assertThrows(ScriptException.class, () -> new Script().run("""
        function f1(a) {
          function nested(b) {
            return b;
          }
          return a;
        }
        nested(42);
        """));
  }

  @Test
  public void assignToVar() {
    new Script().run("""
        function f1() {} var a = f1;
        """);
  }

  @Test
  public void assignToVarExecute() {
    Object result = new Script().run("""
        function f1() {
          println("f1");
        }
        function f1(msg) {
          println(msg);
          return msg;
        }
        function f2(f) {
          f("foo");
        }
        var a = f1;
        var b = f1;
        org.junit.jupiter.api.Assertions.assertTrue(a == b);
        org.junit.jupiter.api.Assertions.assertTrue(a == f1);
        org.junit.jupiter.api.Assertions.assertFalse(a == f2);
        a();
        a("test");
        println(typeof a);
        var result = f2(a);
        """);
    assertEquals("foo", result);
  }

  @Test
  public void findFunctions() {
    new Script().run("""
        function f1() {
          f2("foo");
        }
        function f2(s) {
          println(s);
        }
        f1();
        """);
  }

}
