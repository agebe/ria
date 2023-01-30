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

import java.util.List;

import org.ria.java.JavaC;
import org.ria.java.JavaSource;

public class JavaCTest {

  public static void main(String[] args) throws Exception {
    List<JavaSource> sources = List.of(
        new JavaSource("HelloWorld", """
            public class HelloWorld {
              public static void main(String args[]) {
                System.out.println("This is in another java file");
                bar.Foo.sayHello();
              }
            }
            """),
        new JavaSource("bar.Foo", """
            package bar;
            public class Foo {
              int a = 1;
              public Foo(int b) {
                a = b;
                char c = '}';
                // }
              }
              public static void sayHello() {
                Integer i = new Integer(1);
                Double d = new Double(1.5);
                System.out.println("hello");
                //HelloWorld.main(null);
              }
            }
                """));
    ClassLoader cloader = JavaC.compile(sources, JavaC.class.getClassLoader(), false);
    cloader
    .loadClass("HelloWorld")
    .getDeclaredMethod("main", new Class[] { String[].class })
    .invoke(null, new Object[] { null });
  }

}
