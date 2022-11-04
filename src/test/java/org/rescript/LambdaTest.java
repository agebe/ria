package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LambdaTest {

  @Test
  public void lambdaNoParam() {
    assertEquals(42, new Script().run("""
        function f1(f) {
          f();
        }
        f1(() -> 42);
        """));
  }

  @Test
  public void lambdaWithSingleParam() {
    assertEquals('o', new Script().run("""
        function f1(f) {
          f("foo");
        }
        f1(a -> a.charAt(1));
        """));
  }

  @Test
  public void lambdaWithMultiParam() {
    assertEquals("foobar", new Script().run("""
        function f1(f) {
          f("foo", "bar");
        }
        f1((a,b) -> (a+b));
        """));
  }

  @Test
  public void lambdaWithMultiParamAndBody() {
    assertEquals("foobarfoobarfoobar", new Script().run("""
        function f1(f) {
          f("foo", "bar");
        }
        f1((a,b) -> {
          var s = new StringBuilder();
          for(var i=0;i<3;i++) {
            s.append(a+b);
          }
          return s.toString();
        });
        """));
  }

  @Test
  public void lambdaCallingSiblingScriptFunction() {
    assertEquals('o', new Script().run("""
        function f1(f) {
          f("foo", "bar");
        }
        function f2(s) {
          s.charAt(1);
        }
        f1((a,b) -> {
          f2(a+b);
        });
        """));
  }

  @Test
  public void lambdaWithNestedFunction() {
    assertEquals('f', new Script().evalChar("""
        function f1(f) {
          f("foo", "bar");
        }
        f1((a,b) -> {
          function f2(s) {
            s.charAt(0);
          }
          f2(a+b);
        });
        """));
  }

  @Test
  public void assignLambdaToVar() {
    assertEquals(84, new Script().evalInt("""
        var a = s -> 2*s;
        a(42);
        """));
  }

  @Test
  public void lambdaShouldNotBeExecuted() {
    // This creates an ExpressionStatement so the lambda is evaluated to a function value
    // but not executed
    new Script().run("""
        (a,b) -> a+b;
        """);
  }

}
