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
