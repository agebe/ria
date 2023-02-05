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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class Test1 {

  public static class TestInner1 {

    public static class TestInner3 {
      public static PrintStream out = System.out;
      public static final String CONST = "TI3_CONST";
    }

    public static TestInner2 TI2 = new TestInner2();

    public static boolean f1() {
      System.out.println("f1");
      return true;
    }

    public static void f2(Object o) {
      System.out.println("f2 with '%s'".formatted(o));
    }

    public static String functionWith2Parameters(String p1, String p2) {
      System.out.println("ti1, p1 '%s', p2 '%s'".formatted(p1,p2));
      return p2;
    }
  }

  public static class TestInner2 {
    public static final TestInner1 TI1 = new TestInner1();

    public static String functionWith2Parameters(String p1, String p2) {
      System.out.println("ti2, p1 '%s', p2 '%s'".formatted(p1,p2));
      return p1;
    }

  }

  public static final TestInner2 TI2 = new TestInner2();

  private Script script;

  @BeforeEach
  public void prepareScript() {
    script = new Script()
        .addStaticImport("org.junit.jupiter.api.Assertions.*");
  }

//  private ScriptBuilder base = new ScriptBuilder()
//      .addImport("java.util.Objects")
//      .addStaticImport("org.junit.jupiter.api.Assertions.*");

  @Test
  public void hello() throws Exception {
    script.run("java.lang.System.out.println('Hello World')");
  }

  @Test
  public void helloWithDefaultImport() {
    script.run("System.out.println('Hello World')");
  }

  @Test
  public void importTest() {
    assertEquals("p1.A", script.runReturning("%s.p1.A.test()".formatted(pkg()), String.class));
    assertEquals("p2.A", script.runReturning("%s.p2.A.test()".formatted(pkg()), String.class));
    assertEquals("p1.A", script.addImport("%s.p1.A".formatted(pkg())).runReturning("A.test();", String.class));
    assertEquals("p2.A", script.addImport("%s.p1.A".formatted(pkg()))
        .runReturning("%s.p2.A.test()".formatted(pkg()), String.class));
  }

  private String pkg() {
    return this.getClass().getPackage().getName();
  }

  @Test
  public void fcall() {
    new Script().run("java.lang.System.out.println(%s.Test1.TI2.TI1.f1())".formatted(pkg()));
  }

  @Test
  public void multiParamFCallTrue() {
    boolean result = script.evalPredicate("""
        Objects.equals("123", "123")
        """);
    Assertions.assertTrue(result);
  }

  @Test
  public void multiParamFCallFalse() {
    boolean result = script.evalPredicate("""
        Objects.equals("foo", "bar")
        """);
    Assertions.assertFalse(result);
    Assertions.assertTrue(script.evalPredicate("Objects.equals(\"foo\", \"foo\")"));
  }

  @Test
  public void printNow() {
    script.run("""
        System.out.println(LocalDateTime.now())
        """);
  }

  @Test
  public void importedHello() {
    script.run("println(\"Hello World\")");
  }

  @Test
  public void isBlank() {
    boolean result = script.evalPredicate("""
            org.apache.commons.lang3.StringUtils.isBlank("123")
            """);
    assertFalse(result);
  }

  @Test
  public void staticImports() {
    script
    .addStaticImport("Math.*")
    .addStaticImport("java.time.LocalDateTime.now")
    .addStaticImport("java.time.Instant.now")
    .run("""
        println(max(Double.parseDouble("1.0"), 2d));
        println(now());
        """);
  }

  @Test
  public void runDouble() {
    double pi = script.evalDouble("Math.PI;");
    Assertions.assertEquals(3.14, pi, 0.01);
    double sqrt2 = script.evalDouble("Math.sqrt(2d)");
    Assertions.assertEquals(1.4142, sqrt2, 0.01);
  }

  @Test
  public void runFloat() {
    float f = script.evalFloat("""
            Float.parseFloat("1.23")
            """);
    Assertions.assertEquals(1.23, f, 0.01);
  }

  @Test
  public void boolLiteralTrue() {
    Assertions.assertTrue(script.evalPredicate("true"));
  }

  @Test
  public void boolLiteralFalse() {
    Assertions.assertFalse(script.evalPredicate("false"));
  }

  @Test
  public void boolLiteral() {
    Assertions.assertTrue(script.evalPredicate("Boolean.valueOf(true)"));
  }

  @Test
  public void floatLiteral() {
    Assertions.assertEquals(1.2345f, script.evalFloat("1.2345f"));
  }

  @Test
  public void floatLiteral2() {
    Assertions.assertEquals(1.2345f, script.evalFloat("1.2345f"));
  }


  @Test
  public void doubleLiteral() {
    Assertions.assertEquals(1234.56, script.evalDouble("12_34.5_6d"));
  }

  @Test
  public void intLiteral() {
    Assertions.assertEquals(42, new Script().evalInt("4_2"));
  }

  @Test
  public void intLiteralHex() {
    Assertions.assertEquals(0xABC, new Script().evalInt("0xabc"));
  }

  @Test
  public void intLiteralOct() {
    Assertions.assertEquals(19, new Script().evalInt("023"));
  }

  @Test
  public void intLiteralBin() {
    Assertions.assertEquals(42, new Script().evalInt("0b101010"));
  }

  @Test
  public void longLiteral() {
    Assertions.assertEquals(9223372036854775807l,
        new Script().evalLong("9__223_372_036_854_775_807l"));
  }

  @Test
  public void longMax() {
    Assertions.assertEquals(Long.MAX_VALUE, new Script().evalLong("Long.MAX_VALUE"));
  }

  @Test
  public void runReturning() {
    String s = new Script().runReturning("\"foo\"", String.class);
    Assertions.assertEquals("foo", s);
  }

  @Test
  public void repl() {
    Script script = new Script();
    double d = script.evalDouble("1.23d");
    int i = script.evalInt("1");
    long l = script.evalLong("var l = Long.MAX_VALUE;");
    // make sure variable l persists between script runs
    script.run("org.junit.jupiter.api.Assertions.assertEquals(Long.MAX_VALUE, l)");
    Assertions.assertEquals(1.23, d);
    Assertions.assertEquals(1, i);
    Assertions.assertEquals(Long.MAX_VALUE, l);
  }

  @Test
  public void varassign() {
    boolean b = script.evalPredicate("""
        var b;
        b = true;
        """);
    Assertions.assertTrue(b);
  }

  @Test
  public void emptyStmt() {
    new Script().run(";;;");
  }

  @Test
  public void simpleIfTest() {
    assertEquals(2, new Script().evalInt("1;if(true) 2;"));
    assertEquals(1, new Script().evalInt("1;if(false) 2;"));
  }

  @Test
  public void simpleIfElseTest() {
    int a = script.evalInt("""
        1;
        if(true) 2; else 3;
        """);
    assertEquals(2, a);
  }

  @Test
  public void danglingElseTest() {
    assertEquals(1, script.evalInt("""
        1;
        if(false)
          if(false) 2; else 3;
        """
        ));
    assertEquals(2, script.evalInt("""
        1;
        if(true)
          if(true) 2; else 3;
        """
        ));
    assertEquals(3, script.evalInt("""
        1;
        if(true)
          if(false) 2; else 3;
        """
        ));
  }

  @Test
  public void nestedIfElse() {
    assertEquals(3, script.evalInt("""
        1;
        if(true)
          if(false) 2; else 3;
        else 4;
        """
        ));
  }

  @Test
  public void nonBooleanIfFail() {
    Assertions.assertThrows(ScriptException.class, () -> new Script().run("if(1) true;"));
  }

  @Test
  public void emptyBlock() {
    new Script().run("{}");
  }

  @Test
  public void simpleBlock() {
    new Script().run("{1;}");
  }

  @Test
  public void multiStmtBlock() {
    new Script().run("{1;2;}");
  }

  @Test
  public void emptyNestedBlock() {
    new Script().run("{{}}");
  }

  @Test
  public void emptyNestedBlock2() {
    new Script().run("{{}{}}");
  }

  @Test
  public void ifBlockReturn() {
    assertEquals(3, script.evalInt("""
        1;
        if(true) {
          if(false) {
            2;
          } else {
            return 3;
          }
        } else {
          4;
        }
        5;
        """
        ));
  }

  @Test
  public void elseif() {
    assertEquals(4, script.evalInt("""
        1;
        if(false) {
          return 2;
        } else if(false) {
          return 3;
        } else if(true) {
          return 4;
        } else {
          return 5;
        }
        6;
        """
        ));
  }

  @Test
  public void singleExpression() {
    assertEquals(42, new Script().evalInt("42"));
  }

  @Test
  public void varargs() {
    assertEquals("varargs",
        script.runReturning("""
        String.format("var%s", "args")
        """, String.class));
  }

  @Test
  public void twoMethods() {
    assertEquals("StringLiteral",
        script.runReturning("""
        "myStringLiteral".substring(0).substring(2)
        """, String.class));
  }

  @Test
  public void keyword() {
    // the antlr parser already throws an exception
    // TODO might need to get rid of some keywords in the lexer as java allows
    // variable names like transitive, module etc...
    assertThrows(ScriptException.class, () -> new Script().run("var abstract = 1;"));
  }

  @Test
  public void test1() {
    String s = """
        var foo = %s.Test1.TestInner1::functionWith2Parameters;
        var v1 = "1";
        assertEquals("1", v1);
        var v2 = "2";
        println(v1.equals(v2));
        println(v1);
        println(v2);
        var v3 = v1;
        println(v3);
        println(v3.equals(v1));
        var v4;
        println(v4);
        println("type of v4 is: " + typeof v4);
        var v5 = %s.Test1.TI2.TI1;
        v5.f2(v4);
        var v6 = "v5 value";
        var v7 = foo(v6, "myStringLiteral").substring(2);
        assertEquals("StringLiteral", v7);
        var v8;
        v8 = 12345;
        assertEquals(12345, v8);
        %s.Test1.TestInner1.TestInner3.out.println("test");
        assertEquals("TI3_CONST", %s.Test1.TestInner1.TestInner3.CONST);
        return v1.equals("1");
        // this should not be executed
        assertEquals("2", v1);
          """.formatted(pkg(), pkg(), pkg(), pkg());
    assertTrue(script.evalPredicate(s));
  }

  @Test
  public void main() {
    new Script().run("function main() {println(\"main\");} main();");
  }

  @Test
  @Disabled // disabled as it throws a java.awt.HeadlessException on github actions ci
  public void callWithNull() {
    new Script().run("""
        var f = new javax.swing.JFrame();
        f.setLayout(null);
        """);
  }

  @Test
  @Disabled // disabled as it throws a java.awt.HeadlessException on github actions ci
  public void callWithBorderLayout() {
    new Script().run("""
        var f = new javax.swing.JFrame();
        f.setLayout(new java.awt.BorderLayout());
        """);
  }

}
