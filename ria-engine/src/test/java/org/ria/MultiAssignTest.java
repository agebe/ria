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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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
  public void toManyList() {
    Script script = new Script();
    script.run("var (a,b,c,d) = [42,43];");
    assertEquals(42, script.getVariable("a"));
    assertEquals(43, script.getVariable("b"));
    assertEquals(List.of(42,43), script.getVariable("c"));
    assertEquals(List.of(42,43), script.getVariable("d"));
  }

  @Test
  public void toManyArray() {
    Script script = new Script();
    script.run("var (a,b,c,d) = arrayof [42,43];");
    assertEquals(42, script.getVariable("a"));
    assertEquals(43, script.getVariable("b"));
    assertArrayEquals(new int[] {42,43}, (int[])script.getVariable("c"));
    assertArrayEquals(new int[] {42,43}, (int[])script.getVariable("d"));
  }

  @Test
  public void toFewList() {
    Script script = new Script();
    script.run("var a,b; (a) = [42,43];");
    assertEquals(42, script.getVariable("a"));
  }

  @Test
  public void toFewArray() {
    Script script = new Script();
    script.run("var a,b; (a) = arrayof [42,43];");
    assertEquals(42, script.getVariable("a"));
  }

  @Test
  public void list() {
    Script script = new Script();
    script.run("var (a,b,c,d) = java.util.List.of(42,43);");
    assertEquals(42, script.getVariable("a"));
    assertEquals(43, script.getVariable("b"));
    assertEquals(List.of(42,43), script.getVariable("c"));
    assertEquals(List.of(42,43), script.getVariable("d"));
  }

  @Test
  public void scriptFunction() {
    Script script = new Script();
    script.run("function f1() {[1,2];} var (a,b) = f1();");
    assertEquals(1, script.getVariable("a"));
    assertEquals(2, script.getVariable("b"));
  }

  public static int[] javaArrayHelper() {
    return new int[] {1, 2};
  }

  public static List<Integer> javaListHelper() {
    return List.of(1,2);
  }

  @Test
  public void javaArray() {
    Script script = new Script();
    script.run("var (a,b) = %s.javaArrayHelper();".formatted(this.getClass().getName()));
    assertEquals(1, script.getVariable("a"));
    assertEquals(2, script.getVariable("b"));
  }

  @Test
  public void javaList() {
    Script script = new Script();
    script.run("var (a,b,c) = %s.javaListHelper();".formatted(this.getClass().getName()));
    assertEquals(1, script.getVariable("a"));
    assertEquals(2, script.getVariable("b"));
    assertEquals(List.of(1,2), script.getVariable("c"));
  }

  @Test
  public void javaFields() {
    new Script().run("""
        import static org.junit.jupiter.api.Assertions.assertEquals;
        javasrc '''
        package org.ria.test;
        public class TestClass {
          public int a;
          public int b;
        }
        ''';
        org.ria.test.TestClass t = new org.ria.test.TestClass();
        t.(a,b) = [42,43];
        assertEquals(42, t.a);
        assertEquals(43, t.b);
        t;
        """);
  }

  @Test
  public void javaStaticFields() {
    new Script().run("""
        import static org.junit.jupiter.api.Assertions.assertEquals;
        javasrc '''
        public class TestClass {
          public static int a;
          public static int b;
        }
        ''';
        TestClass.(a,b) = [42,43];
        assertEquals(42, TestClass.a);
        assertEquals(43, TestClass.b);
        """);
  }

}
