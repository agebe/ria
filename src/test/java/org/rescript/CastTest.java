package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CastTest {

  public static class A {}
  public static class B extends A {};
  
  public static A foo() {
    return new B();
  }

  public static boolean bar(B param1) {
    return true;
  }

  @Test
  public void simple() {
    assertEquals("double", new Script("typeof (double)1").run());
  }

  @Test
  public void fromPrimitive() {
    assertEquals("java.lang.Boolean", new Script("typeof (Boolean)true").run());
  }

  @Test
  public void cast() {
    String pkg = this.getClass().getPackage().getName();
    boolean b = new Script("""
        var v = %s.CastTest.foo();
        println(typeof v);
        %s.CastTest.bar((%s.CastTest.B)v);
        """.formatted(pkg, pkg, pkg)).evalPredicate();
    assertTrue(b);
  }

}
