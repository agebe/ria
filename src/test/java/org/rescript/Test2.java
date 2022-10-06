package org.rescript;

import org.junit.jupiter.api.Test;

public class Test2 {

  public static interface I {
    default void foo() {
      System.out.println("I");
    }
  }

  public abstract static class Base {

    public static class Inner {
      public static class Inner2 {
        
      }
    }

    public void foo() {
      System.out.println("Base");
    }
    public static void bar() {
      System.out.println("bar");
    }
  }

  public static class A extends Base implements I {
  }

  public static class B {
    public static class D {
      public static final int MY_INT = 1;
      public final int MY_CONST = 2;
    }
  }

  public static class C {
    public static final B.D MY_D = new B.D();
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

  @Test
  public void forLoop() {
    int i = 1, kk = 4;
    for(int k=1,j=2;i<10;i++,j+=2,kk=k*2,k--) {
      System.out.println("%s, %s, %s, %s".formatted(i, j, k, kk));
    }
    //System.out.println("%s, %s, %s, %s".formatted(i, j, k, kk));
  }

  @Test
  public void forLoop2() {
    int i = 1, kk = 4;
    int k = 1, ii;
    ii = 0;
    System.out.println(ii);
    System.out.println(k);
    System.out.println(kk);
//    for(i=2,int k=1;i<10;i++) {
//      System.out.println("%s, %s, %s, %s".formatted(i, j, k, kk));
//    }
    //System.out.println("%s, %s, %s, %s".formatted(i, j, k, kk));
    for(int jj=1;;i++,k=i>10?i:k) {
      System.out.println(i);
      if(i > 100) {
        System.out.println(jj);
        break;
      }
    }
  }

}
