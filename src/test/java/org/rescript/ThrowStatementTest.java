package org.rescript;

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

  @Test
  public void test() {
    new Script().run("""
        function foo() {
          function bar() {
            var e = new RuntimeException('test');
            e.printStackTrace();
          }
          bar();
        }
        foo();
        """);
  }

}
