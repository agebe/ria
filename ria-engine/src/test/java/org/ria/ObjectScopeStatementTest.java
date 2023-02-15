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

import java.util.List;

import org.junit.jupiter.api.Test;

public class ObjectScopeStatementTest {

  @Test
  public void simple() {
    new Script().run("new Object() {}");
  }

  @Test
  public void expression1() {
    Script script = new Script();
    script.run("""
        javasrc '''

        import java.util.function.Consumer;
        import java.util.List;
        import java.util.ArrayList;

        public class A implements Consumer<Object> {
          public List<Object> l = new ArrayList<>();
          @Override
          public void accept(Object o) {
            //System.out.println(o);
            l.add(o);
          }
        }''';
        var a = new A() {
          '123'
          42
          println('foo')
          1+1
        };
        var l = a.l;
        """);
    Object o = script.getVariable("l");
    assertEquals(List.of("123", 42, 2), o);
  }

  @Test
  public void statement1() {
    Script script = new Script();
    script.run("""
        javasrc $imports + '''
        public class A implements Consumer<Object> {
          public List<Object> l = new ArrayList<>();
          @Override
          public void accept(Object o) {
            //System.out.println(o);
            l.add(o);
          }
        }''';
        var a = new A();
        a {
          '123'
          42
          println('foo')
          1+1
        };
        var l = a.l;
        """);
    Object o = script.getVariable("l");
    assertEquals(List.of("123", 42, 2), o);
  }

  @Test
  public void statement2() {
    Script script = new Script();
    script.run("""
        javasrc '''
        public class A {
          public int i;
          public int i2;
        }''';
        var a = new A();
        var foo = 5;
        a {
          i = 42
          i++
          i2 = foo - 1
        }
        println(a.i);
        var i = a.i;
        var i2 = a.i2;
        """);
    assertEquals(43, script.getVariable("i"));
    assertEquals(4, script.getVariable("i2"));
  }

  @Test
  public void statements() {
    Script script = new Script();
    script.run("""
        javasrc '''
        public class A {
          public int i;
        }''';
        var a = new A() {
          for(int j=0;j<2;j++) {
            println('foo');
            i = 42;
          }
        };
        println(a.i);
        var i = a.i;
        """);
    assertEquals(42, script.getVariable("i"));
  }

  @Test
  public void statements2() {
    Script script = new Script();
    script.run("""
        javasrc '''
        public class A {
          public int i;
        }''';
        var a = new A();
        a {
          for(int j=0;j<2;j++) {
            println('foo');
            i = 42;
          }
        }
        println(a.i);
        var i = a.i;
        """);
    assertEquals(42, script.getVariable("i"));
  }

  @Test
  public void expressions() {
    new Script().run("""
        import static org.junit.jupiter.api.Assertions.*;
        javasrc $imports + '''
        public class A implements Consumer<Object> {
          private String test;
          private List<Object> l = new ArrayList<>();
          public A(String s) {
            this.test = s;
          }
          public String test() {
            return test;
          }
          public int testInt() {
            return 99;
          }
          public List<Object> getL() {
            return l;
          }
          @Override
          public void accept(Object o) {
            System.out.println(o);
            l.add(o);
          }
        }''';
        javasrc '''
        public class B {
          private int myInt;
          public B() {
          }
          public B(int i) {
            this.myInt = i;
          }
          public void setMyInt(int i) {
            myInt = i;
          }
          public int getMyInt() {
            return myInt;
          }
          @Override
          public boolean equals(Object other) {
            if(other instanceof B b) {
              return this.myInt == b.myInt;
            } else {
              return false;
            }
          }
          @Override
          public int hashCode() {
            return myInt;
          }
          @Override
          public String toString() {
            return "B:" + myInt;
          }
        }''';
        function lower(s) {
          s.toLowerCase();
        }
        var a = new A('foo') {
          'S1'
          testInt()
          lower('S2')
          new B() {
            setMyInt(11)
            println(getMyInt())
          }
          new B() {
            setMyInt(testInt());
          }
          42
        };
        a {
        }
        assertEquals(List.of('S1', 99, 's2', new B(11), new B(99), 42), a.getL());
        """);
  }

  @Test
  public void functions() {
    new Script().run("""
        javasrc '''
          public class B {
            private String s;
            public void set(String s) {
              System.out.println("set on B " + s);
              this.s = s;
            }
          }
        ''';
        javasrc $imports + '''
        public class A implements Consumer<Object> {
          @Override
          public void accept(Object o) {
            System.out.println("accept " + o);
          }
          public String create(String s) {
          System.out.println("create " + s);
            return s;
          }
          public List<String> list(String s) {
            System.out.println("create list of " + s);
            return List.of(s);
          }
          public B b() {
            System.out.println("create B");
            return new B();
          }
        }''';
        var a = new A() {
          create('123')
          create('abc')
          list('def')
          b() {
            set('zzz')
          }
        };
        println(a);
        """);
  }

}
