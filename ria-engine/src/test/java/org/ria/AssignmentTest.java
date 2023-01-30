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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class AssignmentTest {

  @Test
  public void addAssign() {
    assertEquals("foobar", new Script().run("var a = 'foo';a+='bar';a;"));
  }

  @Test
  public void addAssign2() {
    assertEquals(42, new Script().run("var a = 21;a+=21;"));
  }

  @Test
  public void addAssignFail() {
    // variable a is unknown, must be initialized first
    assertThrows(ScriptException.class, () -> new Script().run("a += 21;"));
  }

  @Test
  public void subAssign() {
    assertEquals(-2, new Script().run("var a = 4;a-=6;"));
  }

  @Test
  public void mulAssign() {
    assertEquals(24, new Script().run("var a = 4;a*=6;"));
  }

  @Test
  public void divAssign() {
    assertEquals(5, new Script().run("var a = 10;a/=2;"));
  }

  @Test
  public void modAssign() {
    assertEquals(3, new Script().run("var a = 11;a%=4;"));
  }

  @Test
  public void andAssign() {
    assertEquals(3, new Script().run("var a = 35;a &= 15;"));
  }

  @Test
  public void orAssign() {
    assertEquals(3, new Script().run("var a = 1;a |= 3;"));
  }

  @Test
  public void xorAssign() {
    assertEquals(3, new Script().run("var a = 1;a ^= 2;"));
  }

  @Test
  public void lshiftAssign() {
    assertEquals(6, new Script().run("var a = 3;a <<= 1;"));
  }

  @Test
  public void rshiftAssign() {
    assertEquals(2, new Script().run("var a = 8;a >>= 2;a;"));
  }

  @Test
  public void urshiftAssign() {
    assertEquals(2, new Script().run("var a = 8;a >>>= 2;a;"));
  }

  @Test
  public void javaField() {
    int a = new Script().evalInt("""
        javasrc '''
        package org.ria.test;
        public class TestClass {
          public int a;
        }
        ''';
        var a = new org.ria.test.TestClass();
        a.a = 42;
        a.a;
        """);
    assertEquals(42, a);
  }

  @Test
  public void javaStaticField() {
    int a = new Script().evalInt("""
        javasrc '''
        public class TestClass {
          public static int a;
        }
        ''';
        TestClass.a = 42;
        TestClass.a;
        """);
    assertEquals(42, a);
  }

}
