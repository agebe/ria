package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
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

  private ScriptBuilder base = new ScriptBuilder()
      .addImport("java.util.Objects")
      .addStaticImport("org.junit.jupiter.api.Assertions.*")
      .addFunctionAlias("println", "System.out.println");

  @Test
  public void hello() throws Exception {
    base.setScript("java.lang.System.out.println(\"Hello World\")").create().run();
  }

  @Test
  public void helloWithDefaultImport() {
    base.setScript("System.out.println(\"Hello World\")").create().run();
  }

  @Test
  public void importTest() {
    assertEquals("p1.A", base
        .setScript("org.rescript.p1.A.test()")
        .create()
        .runReturning(String.class));
    assertEquals("p2.A", base
        .setScript("org.rescript.p2.A.test()")
        .create()
        .runReturning(String.class));
    assertEquals("p1.A", base
        .addImport("org.rescript.p1.A")
        .setScript("A.test();")
        .create()
        .runReturning(String.class));
    assertEquals("p2.A", base
        .addImport("org.rescript.p1.A")
        .setScript("org.rescript.p2.A.test()")
        .create()
        .runReturning(String.class));
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
    boolean result = base.setScript("""
        Objects.equals("123", "123")
        """)
    .create()
    .evalPredicate();
    Assertions.assertTrue(result);
  }

  @Test
  public void multiParamFCallFalse() {
    boolean result = base.setScript("""
        Objects.equals("foo", "bar")
        """)
    .create()
    .evalPredicate();
    Assertions.assertFalse(result);
    Assertions.assertTrue(base.create().evalPredicate("Objects.equals(\"foo\", \"foo\")"));
  }

  @Test
  public void printNow() {
    base.setScript("""
        System.out.println(LocalDateTime.now())
        """)
    .addImport("java.time.*")
    .create()
    .run();
  }

//  @Test
  public void importedHello() {
    base
    .setScript("println(\"Hello World\")")
    .create()
    .run();
  }

  @Test
  public void isBlank() {
    boolean result = base.setScript("""
            org.apache.commons.lang3.StringUtils.isBlank("123")
            """)
        .create()
        .evalPredicate();
    assertFalse(result);
  }

  @Test
  public void staticImports() {
    base.setScript("""
        println(max(Double.parseDouble("1.0"), 2d));
        println(now());
        """)
    .addStaticImport("Math.*")
    .addStaticImport("java.time.LocalDateTime.now")
    .addStaticImport("java.time.Instant.now")
    .create()
    .run();
  }

  @Test
  public void runDouble() {
    double pi = base.setScript("Math.PI;").create().evalDouble();
    Assertions.assertEquals(3.14, pi, 0.01);
    double sqrt2 = base.setScript("Math.sqrt(2d)").create().evalDouble();
    Assertions.assertEquals(1.4142, sqrt2, 0.01);
  }

  @Test
  public void runFloat() {
    float f = base.setScript("""
        Float.parseFloat("1.23")
        """)
        .create()
        .evalFloat();
    Assertions.assertEquals(1.23, f, 0.01);
  }

  @Test
  public void boolLiteralTrue() {
    boolean b = base.setScript("""
        true
        """)
        .create()
        .evalPredicate();
    Assertions.assertTrue(b);
  }

  @Test
  public void boolLiteralFalse() {
    boolean b = base.setScript("""
        false
        """)
        .create()
        .evalPredicate();
    Assertions.assertFalse(b);
  }

  @Test
  public void boolLiteral() {
    boolean b = base.setScript("""
        Boolean.valueOf(true)
        """)
        .create()
        .evalPredicate();
    Assertions.assertTrue(b);
  }

  @Test
  public void floatLiteral() {
    float f = base
        .setScript("1.2345f")
        .create()
        .evalFloat();
    Assertions.assertEquals(1.2345f, f);
  }

  @Test
  public void floatLiteral2() {
    float f = base
        .setScript("1.2345f")
        .create()
        .evalFloat();
    Assertions.assertEquals(1.2345f, f);
  }


  @Test
  public void doubleLiteral() {
    double d = base
        .setScript("12_34.5_6d")
        .create()
        .evalDouble();
    Assertions.assertEquals(1234.56, d);
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
    boolean b = base.setScript("""
        var b;
        b = true;
        """)
        .create()
        .evalPredicate();
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
    int a = base.create().evalInt("""
        1;
        if(true) 2; else 3;
        """);
    assertEquals(2, a);
  }

  @Test
  public void danglingElseTest() {
    assertEquals(1, base.create().evalInt("""
        1;
        if(false)
          if(false) 2; else 3;
        """
        ));
    assertEquals(2, base.create().evalInt("""
        1;
        if(true)
          if(true) 2; else 3;
        """
        ));
    assertEquals(3, base.create().evalInt("""
        1;
        if(true)
          if(false) 2; else 3;
        """
        ));
  }

  @Test
  public void nestedIfElse() {
    assertEquals(3, base.create().evalInt("""
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
    assertEquals(3, base.create().evalInt("""
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
    assertEquals(4, base.create().evalInt("""
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
        base.setScript("""
        String.format("var%s", "args")
        """)
        .create()
        .runReturning(String.class));
  }

  @Test
  public void twoMethods() {
    assertEquals("StringLiteral",
        base.setScript("""
        "myStringLiteral".substring(0).substring(2)
        """)
        .create()
        .runReturning(String.class));
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
    String script = """
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
        var v5 = %s.Test1.TI2.TI1;
        v5.f2(v4);
        var v6 = "v5 value";
        var v7 = foo(v6, "myStringLiteral").substring(2);
        assertEquals("StringLiteral", v7);
        var v8;
        v8 = 12345;
        assertEquals(12345, v8);
        org.rescript.Test1.TestInner1.TestInner3.out.println("test");
        assertEquals("TI3_CONST", org.rescript.Test1.TestInner1.TestInner3.CONST);
        return v1.equals("1");
        // this should not be executed
        assertEquals("2", v1);
          """.formatted(pkg());
    boolean result = base
    .addFunctionAlias("foo", "org.rescript.Test1.TestInner1.functionWith2Parameters")
    .setScript(script)
    .create()
    .evalPredicate();
    assertTrue(result);
  }
}
