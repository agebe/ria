package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class FunctionTest {

  @Test
  public void simple() {
    assertNull(new Script().run("function f1() {}"));
  }

  @Test
  public void simple2() {
    assertNull(new Script().run("function f1() {42;}"));
  }

  @Test
  public void simple2WithCall() {
    assertEquals(42, new Script().run("function f1() {42;} f1();"));
  }

  @Test
  public void withParams() {
    assertEquals(4, new Script().run("function f1(a) {a*2;} f1(2);"));
  }

  @Test
  public void withMultiParams() {
    assertEquals(8, new Script().run("function f1(a,b) {Math.pow(a,b);} f1(2,3);"));
  }

  @Test
  public void withMultiFunctions() {
    assertEquals("f2", new Script().run("function f1(a,b,c) {c;} function f2(a) {a;} f2(\"f2\");"));
  }

  @Test
  public void nested() {
    assertEquals(2, new Script().run("""
        function f1(a) {
          function f2(b) {
            a+b;
          }
          f2(b);
        }
        f1(1);
        """));
  }

  @Test
  public void recusive() {
    assertEquals(120, new Script().run("""
        function fac(num) {
          num==0?1:num*fac(num-1);
        }
        fac(5);
        """));
  }

  @Test
  public void withReturnStatement() {
    // TODO
  }

  @Test
  public void withLoopBreak() {
    // TODO
  }

  @Test
  public void withLoopContinue() {
    // TODO
  }

}
