package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class ThrowStatementTest {

  @Test
  public void simple() {
    assertThrows(NoSuchElementException.class, () ->new Script().run("throw new NoSuchElementException('test');"));
  }

  @Test
  public void checkedException() {
    assertThrows(IOException.class, () -> new Script().runUnwrapException("throw new IOException('test');"));
  }

  @Test
  public void checkedExceptionWrapper() {
    assertThrows(CheckedExceptionWrapper.class, () -> new Script().run("throw new IOException('test');"));
  }

  @Test
  public void throwErorr() {
    assertThrows(AssertionError.class, () -> new Script().run("throw new AssertionError('test');"));
  }

  public static void checkStackTrace(Throwable t) {
    assertEquals(3, t.getStackTrace().length);
    assertEquals("bar", t.getStackTrace()[0].getMethodName());
    assertEquals("foo", t.getStackTrace()[1].getMethodName());
    assertEquals("__main", t.getStackTrace()[2].getMethodName());
  }

  @Test
  public void scriptStackTrace() {
    new Script().run("""
        function foo() {
          function bar() {
            var t = new RuntimeException('test');
            t.printStackTrace();
            org.ria.ThrowStatementTest.checkStackTrace(t);
          }
          bar();
        }
        foo();
        """);
  }

  @Test
  public void scriptLambda() {
    assertThrows(RuntimeException.class, () -> new Script().run("""
        function foo() {
          function bar() {
            var runnable = () -> org.ria.ThrowStatementTest.testMethod();
            runnable();
          }
          bar();
        }
        foo();
        """));
  }

  private static void somewhereDeeperDownTheStack() {
    throw new RuntimeException("test");
  }

  public static void testMethod() throws Exception {
    somewhereDeeperDownTheStack();
  }

  @Test
  public void scriptJava() {
    assertThrows(RuntimeException.class, () -> new Script().run("""
        org.ria.ThrowStatementTest.testMethod();
        """));
  }

  @Test
  public void scriptJavaMethodRef() {
    assertThrows(RuntimeException.class, () -> new Script().run("""
        var v = org.ria.ThrowStatementTest::testMethod;
        println(typeof v);
        v();
        """));
  }

}
