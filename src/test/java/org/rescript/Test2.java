package org.rescript;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class Test2 {

  public static interface I {
    default void foo() {
      System.out.println("I");
    }
  }

  public abstract static class Base {
    public void foo() {
      System.out.println("Base");
    }
    public static void bar() {
      System.out.println("bar");
    }
  }

  public static class A extends Base implements I {
  }

  @Test
  public void test() {
//    String TestInner1 = "foo";
//    TestInner1 t1 =  new TestInner1();
//    TestInner2 t2;
//    TestInner3 t3;
//    System.out.println(TestInner1.class);
    int a;
    if((a = 42) == 42) {
      System.out.println(a);
    }
//    System.out.println(Object.class.isAssignableFrom(Integer.class));
    System.out.println(Object.class.isAssignableFrom(int.class));
    System.out.println(Integer.class.isAssignableFrom(int.class));
    System.out.println(int.class.isAssignableFrom(Integer.class));
    System.out.println(int.class.isAssignableFrom(long.class));
    System.out.println(long.class.isAssignableFrom(int.class));
    System.out.println(Integer.class.isAssignableFrom(Long.class));
    System.out.println(Long.class.isAssignableFrom(Integer.class));
//    System.out.println(Long.class.isAssignableFrom(Long.class));
  }

  @SuppressWarnings("static-access")
  @Test
  public void method() {
    A a = new A();
    a.foo();
    a.bar();
    A.bar();
    Base.bar();
  }

}
