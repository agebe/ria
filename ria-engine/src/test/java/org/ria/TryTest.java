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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TryTest {

  @Test
  public void simple() {
    assertEquals(1, new Script().evalInt("try {1;} catch(Exception e) {2;}"));
  }

  @Test
  public void simpleCatch() {
    assertEquals(2, new Script().evalInt("try {1;throw new Exception('test');} catch(Exception e) {2;}"));
  }

  @Test
  public void twoCatchBlocks() {
    assertEquals(3, new Script().evalInt("""
        try {
          1;
          throw new Exception('test');
        } catch(RuntimeException e) {
          2;
        } catch(Exception e) {
          3;
        }
        """));
  }

  @Test
  public void notHandledException() {
    assertThrows(CloneNotSupportedException.class, () -> new Script().runUnwrapException("""
        try {
          1;
          throw new CloneNotSupportedException('test');
        } catch(RuntimeException e) {
          2;
        }
        """));
  }

  @Test
  public void notHandledMultiCatch() {
    assertThrows(CloneNotSupportedException.class, () -> new Script().runUnwrapException("""
        try {
          1;
          throw new CloneNotSupportedException('test');
        } catch(ArithmeticException|ArrayIndexOutOfBoundsException|IllegalStateException e) {
          2;
        }
        """));
  }

  @Test
  public void multiCatch() {
    assertEquals(2, new Script().evalInt("""
        try {
          1;
          throw new CloneNotSupportedException('test');
        } catch(RuntimeException|CloneNotSupportedException e) {
          2;
        }
        """));
  }

  @Test
  public void multimultiCatch() {
    assertEquals(3, new Script().evalInt("""
        try {
          1;
          throw new CloneNotSupportedException('test');
        } catch(ArithmeticException|ArrayIndexOutOfBoundsException|IllegalStateException e) {
          2;
        } catch(ArrayStoreException|CloneNotSupportedException|InterruptedException e) {
          3;
        }
        """));
  }

  @Test
  public void rethrow() {
    assertThrows(IllegalStateException.class, () -> new Script().evalInt("""
        try {
          1;
          throw new IllegalStateException('test');
        } catch(Exception e) {
          2;
          throw e;
        }
        """));
  }

  @Test
  public void throwWithCause() {
    assertThrows(TestException.class, () -> new Script().runUnwrapException("""
        try {
          1;
          throw new IllegalStateException('test');
        } catch(Exception e) {
          2;
          throw new org.ria.TestException('test with cause', e);
        }
        """));
  }

  @Test
  public void throwInFunction() {
    assertThrows(IllegalStateException.class, () -> new Script().evalInt("""
        function foo() {
          throw new IllegalStateException('test');
        }
        foo();
        """));
  }

  @Test
  public void catchThrowInFunction() {
    assertEquals(42, new Script().evalInt("""
        function bar() {
          foo();
        }
        function foo() {
          throw new IllegalStateException('test');
        }
        try {
          bar();
        } catch(IllegalStateException e) {
          e.printStackTrace();
          42;
        }
        """));
  }

  @Test
  public void tryWithResource() {
    assertEquals(42, new Script().evalInt("""
        import static org.junit.jupiter.api.Assertions.assertFalse;
        try(var in = new org.ria.TestAutoCloseable()) {
          assertFalse(in.isClosed());
          42;
        }
        """));
  }

  @Test
  public void multiTryWithResource() {
    assertEquals(42, new Script().evalInt("""
        import static org.junit.jupiter.api.Assertions.assertFalse;
        try(
        var in = new org.ria.TestAutoCloseable();
        org.ria.TestAutoCloseable in2 = new org.ria.TestAutoCloseable()) {
          assertFalse(in.isClosed());
          assertFalse(in2.isClosed());
          42;
        }
        """));
  }

  @Test
  public void multiTryWithResource3() {
    assertEquals(42, new Script().evalInt("""
        import static org.junit.jupiter.api.Assertions.assertFalse;
        try(
        var in = new org.ria.TestAutoCloseable();
        org.ria.TestAutoCloseable in2 = new org.ria.TestAutoCloseable();
        var in3 = null) {
          assertFalse(in.isClosed());
          assertFalse(in2.isClosed());
          42;
        }
        """));
  }

  @Test
  public void tryWithResourceExceptionInClose() throws Throwable {
    assertThrows(TestException.class, () -> new Script().runUnwrapException("""
        import static org.junit.jupiter.api.Assertions.assertFalse;
        try(var in = new org.ria.TestAutoCloseableWithException()) {
          assertFalse(in.isClosed());
          42;
        }
        """));
  }

  @Test
  public void tryWithResourceExceptionInCloseSuppressed() throws Throwable {
    try {
      new Script().runUnwrapException("""
          try(var in = new org.ria.TestAutoCloseableWithException()) {
            throw new org.ria.TestException('test');
          }
          """);
    } catch(TestException e) {
      assertEquals("test", e.getMessage());
      assertEquals(1, e.getSuppressed().length);
      assertEquals("exception on close (test)", e.getSuppressed()[0].getMessage());
    }
  }

  @Test
  public void tryWithResourceExceptionInCloseSuppressed2() throws Throwable {
    try {
      new Script().runUnwrapException("""
          try(var in = new org.ria.TestAutoCloseableWithException();
          var in2 = new org.ria.TestAutoCloseableWithException()) {
            throw new org.ria.TestException('test');
          }
          """);
    } catch(TestException e) {
      assertEquals("test", e.getMessage());
      assertEquals(2, e.getSuppressed().length);
      assertEquals("exception on close (test)", e.getSuppressed()[0].getMessage());
      assertEquals("exception on close (test)", e.getSuppressed()[1].getMessage());
    }
  }

  @Test
  public void tryFinally() {
    assertEquals(1, new Script().evalInt("""
        try {
          return 1;
        } finally {
          return 2;
        }
        """));
  }

  @Test
  public void tryFinallyInFunction() {
    // return statements in finally blocks abruptly end the finally block but the return value is ignored
    assertEquals(1, new Script().evalInt("""
        import static org.junit.jupiter.api.Assertions.assertFalse;
        import static org.junit.jupiter.api.Assertions.assertTrue;
        var finallyExecuted = false;
        function foo() {
          try {
            return 1;
          } finally {
            finallyExecuted = true;
            return 2;
            finallyExecuted = false;
          }
          3;
        }
        assertFalse(finallyExecuted);
        try {
          foo();
        } finally {
          assertTrue(finallyExecuted);
        }
        """));
  }

  @Test
  public void tryFinallyInFunction2() {
    assertEquals(3, new Script().evalInt("""
        import static org.junit.jupiter.api.Assertions.assertFalse;
        import static org.junit.jupiter.api.Assertions.assertTrue;
        var finallyExecuted = false;
        function foo() {
          try {
            1;
          } finally {
            finallyExecuted = true;
            return 2;
            finallyExecuted = false;
          }
          3;
        }
        assertFalse(finallyExecuted);
        try {
          foo();
        }
        finally {
          assertTrue(finallyExecuted);
        }
        """));
  }

  @Test
  public void tryFinallyInFunction3() {
    assertEquals(1, new Script().evalInt("""
        import static org.junit.jupiter.api.Assertions.assertFalse;
        import static org.junit.jupiter.api.Assertions.assertTrue;
        var finallyExecuted = false;
        function foo() {
          try {
            throw Exception('test');
          } catch(Exception e) {
            return 1;
          } finally {
            finallyExecuted = true;
            return 2;
            finallyExecuted = false;
          }
          3;
        }
        assertFalse(finallyExecuted);
        try {
          foo();
        } finally {
          assertTrue(finallyExecuted);
        }
        """));
  }

  @Test
  public void exceptionInFinally() {
    assertThrows(RuntimeException.class, () -> new Script().evalInt("""
        import static org.junit.jupiter.api.Assertions.assertFalse;
        import static org.junit.jupiter.api.Assertions.assertTrue;
        var finallyExecuted = false;
        function foo() {
          try {
            return 1;
          } catch(Exception e) {
            return 2;
          } finally {
            finallyExecuted = true;
            throw new RuntimeException('exception in finally block');
            finallyExecuted = false;
          }
          3;
        }
        assertFalse(finallyExecuted);
        try {
          foo();
        } finally {
          assertTrue(finallyExecuted);
        }
        """));
  }

}
