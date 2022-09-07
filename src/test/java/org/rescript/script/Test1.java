package org.rescript.script;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rescript.ScriptBuilder;

public class Test1 {

  public static class TestInner1 {
    public static boolean f1() {
      System.out.println("f1");
      return true;
    }
  }

  public static class TestInner2 {
    public static final TestInner1 TI1 = new TestInner1();
  }

  public static final TestInner2 TI2 = new TestInner2();

  private ScriptBuilder base = new ScriptBuilder()
      .addImport("java.util.Objects")
      .addFunctionAlias("println", "System.out.println");

  @Test
  public void hello() {
    base.setScript("java.lang.System.out.println(\"Hello World\");").create().run();
  }

  @Test
  public void helloWithDefaultImport() {
    base.setScript("System.out.println(\"Hello World\");").create().run();
  }

  @Test
  public void fcall() {
    String pkg = this.getClass().getPackage().getName();
    base.setScript("""
        java.lang.System.out.println(%s.Test1.TI2.TI1.f1());
        """.formatted(pkg))
    .create()
    .run();
  }

  @Test
  public void multiParamFCallTrue() {
    boolean result = base.setScript("""
        Objects.equals("123", "123");
        """)
    .create()
    .evalPredicate();
    Assertions.assertTrue(result);
  }

  @Test
  public void multiParamFCallFalse() {
    boolean result = base.setScript("""
        Objects.equals("foo", "bar");
        """)
    .create()
    .evalPredicate();
    Assertions.assertFalse(result);
  }

  @Test
  public void printNow() {
    base.setScript("""
        System.out.println(LocalDateTime.now());
        """)
    .addImport("java.time.*")
    .create()
    .run();
  }

//  @Test
  public void importedHello() {
    base
    // TODO add support for import and static imports (do we need to make a difference?)
    // importing automatically allows access
    //.import("System.out.*")
    .setScript("println(\"Hello World\");")
    .create()
    .run();
  }

  @Test
  public void isBlank() {
    boolean result = base.setScript("""
            org.apache.commons.lang3.StringUtils.isBlank("123");
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
    double sqrt2 = base.setScript("Math.sqrt(2d);").create().evalDouble();
    Assertions.assertEquals(1.4142, sqrt2, 0.01);
  }

  @Test
  public void runFloat() {
    float f = base.setScript("""
        // TODO
        //println(Float.parseFloat("1.23").class);
        Float.parseFloat("1.23");
        """)
        .create()
        .evalFloat();
    Assertions.assertEquals(1.23, f, 0.01);
  }

  @Test
  public void boolLiteralTrue() {
    boolean b = base.setScript("""
        true;
        """)
        .create()
        .evalPredicate();
    Assertions.assertTrue(b);
  }

  @Test
  public void boolLiteralFalse() {
    boolean b = base.setScript("""
        false;
        """)
        .create()
        .evalPredicate();
    Assertions.assertFalse(b);
  }

  @Test
  public void boolLiteral() {
    boolean b = base.setScript("""
        Boolean.valueOf(true);
        """)
        .create()
        .evalPredicate();
    Assertions.assertTrue(b);
  }

  @Test
  public void floatLiteral() {
    float f = base
        .setScript("1.2345f;")
        .create()
        .evalFloat();
    Assertions.assertEquals(1.2345f, f);
  }

  @Test
  public void floatLiteral2() {
    float f = base
        .setScript("1.2345f;")
        .create()
        .evalFloat();
    Assertions.assertEquals(1.2345f, f);
  }


  @Test
  public void doubleLiteral() {
    double d = base
        .setScript("12_34.5_6d;")
        .create()
        .evalDouble();
    Assertions.assertEquals(1234.56, d);
  }

  @Test
  public void intLiteral() {
    int i = base
        .setScript("4_2;")
        .create()
        .evalInt();
    Assertions.assertEquals(42, i);
  }

  @Test
  public void intLiteralHex() {
    int i = base
        .setScript("0xabc;")
        .create()
        .evalInt();
    Assertions.assertEquals(0xABC, i);
  }

  @Test
  public void intLiteralOct() {
    int i = base
        .setScript("023;")
        .create()
        .evalInt();
    Assertions.assertEquals(19, i);
  }

  @Test
  public void intLiteralBin() {
    int i = base
        .setScript("0b101010;")
        .create()
        .evalInt();
    Assertions.assertEquals(42, i);
  }

  @Test
  public void longLiteral() {
    long l = base
        .setScript("9__223_372_036_854_775_807l;")
        .create()
        .evalLong();
    Assertions.assertEquals(9223372036854775807l, l);
  }

  @Test
  public void longMax() {
    long l = base
        .setScript("Long.MAX_VALUE;")
        .create()
        .evalLong();
    Assertions.assertEquals(Long.MAX_VALUE, l);
  }

//  @Test
  public void test1() {
    String script = """
      var v1;
      var v2 = foo(myVar, "myStringLiteral");
      var v3 = "12345";
      v1 = v2;
      // dot operator not supported yet
      System.out.println("Hello World");
      return v1.equals("12345");
        """;
    new ScriptBuilder().setScript(script).create().run();
  }
}
