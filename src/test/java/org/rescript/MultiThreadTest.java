package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class MultiThreadTest {

  @Test
  public void simple() {
    new Script().run("""
        IntStream.range(0,1000).parallel().forEach(i -> println(
          "%s %s".formatted(Thread.currentThread().getName(), i)))
        """);
  }

  @Test
  public void thread() {
    new Script().run("""
        var i = 42;
        Thread t = new Thread(() -> {
          println(i);
        }, "test");
        t.start();
        t.join();
        """);
  }

  @Test
  public void threadFunction() {
    assertEquals(1,1);
    Object o = new Script().run("""
        var ex = null;
        function f(i) {
          Thread t = new Thread(() -> {
            println("thread '%s', %s".formatted(Thread.currentThread().getName(), Integer.toString(i)));
            org.junit.jupiter.api.Assertions.assertEquals(42,i);
          });
          t.setDefaultUncaughtExceptionHandler((td,e) -> {
            ex = e;
          });
          t.start();
          t.join();
        }
        f(42);
        ex;
        """);
    assertNull(o);
  }


}
