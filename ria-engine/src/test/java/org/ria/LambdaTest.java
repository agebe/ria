package org.ria;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

  @Test
  public void scriptLambdaToJava1() {
    assertArrayEquals(new int[] {0, 1, 2}, (int[])new Script().run("""
        var foo = 2;
        java.util.List.of(42,2,99).stream()
          .filter(i -> i.equals(foo))
          .flatMapToInt(i -> java.util.stream.IntStream.rangeClosed(0,i))
          .toArray();
        """));
  }

  @Test
  public void scriptLambdaToJava2() {
    assertEquals("2", new Script().run("""
        java.util.List.of(42,2,99).stream()
          .filter(i -> i.equals(2))
          .map(i -> i.toString())
          .findFirst()
          .orElse(null);
        """));
  }

  @Test
  public void accessOuter() {
    assertEquals(2, new Script().run("""
        var v = 2;
        java.util.List.of(42,10,99).stream()
          .map(i -> v)
          .findFirst()
          .orElse(null);
        """));
  }

  @Test
  public void accessOuterFunction() {
    assertEquals(2, new Script().run("""
        function test(v) {
          return java.util.List.of(42,10,99).stream()
            .map(i -> v)
            .findFirst()
            .orElse(null);
        }
        test(2);
        """));
  }

}
