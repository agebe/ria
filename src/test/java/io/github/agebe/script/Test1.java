package io.github.agebe.script;

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

  @Test
  public void hello() {
    new RestrictedScriptBuilder().setScript("java.lang.System.out.println(\"Hello World\");").create().run();
  }

  //@Test
  public void helloWithDefaultImport() {
    new RestrictedScriptBuilder().setScript("System.out.println(\"Hello World\");").create().run();
  }

  @Test
  public void fcall() {
    new RestrictedScriptBuilder().setScript("""
        java.lang.System.out.println(io.github.agebe.script.Test1.TI2.TI1.f1());
        """)
    .create()
    .run();
  }

  @Test
  public void multiParamFCall() {
    new RestrictedScriptBuilder().setScript("""
        java.util.Objects.equals("123", "123");
        """)
    .create()
    .run();
  }

//  @Test
  public void importedHello() {
    new RestrictedScriptBuilder()
    // TODO add support for import and static imports (do we need to make a difference?)
    // importing automatically allows access
    //.import("System.out.*")
    .setScript("println(\"Hello World\");")
    .create()
    .run();
  }

  @Test
  public void isBlank() {
    boolean result = new RestrictedScriptBuilder()
        .setScript("""
            org.apache.commons.lang3.StringUtils.isBlank("123");
            """)
        .create()
        .evalPredicate();
    assertFalse(result);
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
    new RestrictedScriptBuilder().setScript(script).create().run();
  }
}
