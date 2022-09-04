package io.github.agebe.script;

import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

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
    base.setScript("""
        java.lang.System.out.println(io.github.agebe.script.Test1.TI2.TI1.f1());
        """)
    .create()
    .run();
  }

  @Test
  public void multiParamFCall() {
    base.setScript("""
        Objects.equals("123", "123");
        """)
    .create()
    .run();
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
    tan(1.1);
    max(0, 0);
    base.setScript("""
        max(Double.parseDouble("1.0"), Double.parseDouble("2.0"));
        println(now());
        """)
    .addStaticImport("Math.*")
    .addStaticImport("java.time.LocalDateTime.now")
    .addStaticImport("java.time.Instant.now")
    .create()
    .run();
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
