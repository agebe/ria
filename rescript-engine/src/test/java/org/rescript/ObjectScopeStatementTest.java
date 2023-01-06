package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Disabled;
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
        public class A implements Consumer<Object> {
          public List<Object> l = new ArrayList<>();
          @Override
          public void accept(Object o) {
            //System.out.println(o);
            l.add(o);
          }
        }
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
        public class A implements Consumer<Object> {
          public List<Object> l = new ArrayList<>();
          @Override
          public void accept(Object o) {
            //System.out.println(o);
            l.add(o);
          }
        }
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
        public class A {
          public int i;
          public int i2;
        }
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
        public class A {
          public int i;
        }
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
        public class A {
          public int i;
        }
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
  @Disabled
  public void expressions() {
    new Script().run("""
        public class A implements Consumer<Object> {
          private String test;
          public A(String s) {
            this.test = s;
          }
          public String test() {
            return test;
          }
          @Override
          public void accept(Object o) {
            System.out.println(o);
          }
        }
        public class B {
          private int myInt;
          public void setMyInt(int i) {
            myInt = i;
          }
          public int getMyInt() {
            return myInt;
          }
          @Override
          public String toString() {
            return "" + myInt;
          }
        }
        function lower(s) {
          s.toLowerCase();
        }
        var a = new A('foo') {
          'S1'
          lower('S2')
          new B() {
            setMyInt(11)
            println(getMyInt())
          }
          42
        };
        //a {
        //}
        """);
  }

}
