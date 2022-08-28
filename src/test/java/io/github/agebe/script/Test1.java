package io.github.agebe.script;

import org.junit.jupiter.api.Test;

public class Test1 {

  private static final String SCRIPT = """
      var v1;
      var v2 = foo(myVar, "myStringLiteral");
      var v3 = "12345";
      v1 = v2;
      // dot operator not supported yet
      //foo
      return v1.equals("12345");
      """;

  @Test
  public void test1() {
    new RestrictedScriptBuilder().setScript(SCRIPT).create().run();
  }
}
