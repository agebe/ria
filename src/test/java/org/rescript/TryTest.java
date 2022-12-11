package org.rescript;

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
          throw new org.rescript.TestException('test with cause', e);
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

}
