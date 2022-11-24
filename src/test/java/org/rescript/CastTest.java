package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
  public void toPrimitive() {
    assertEquals("boolean", new Script("typeof (boolean)Boolean.TRUE").run());
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

  @Test
  public void negative() {
    String pkg = this.getClass().getPackage().getName();
    assertThrows(ScriptException.class, () -> new Script("""
        var v = new %s.CastTest.A();
        println(typeof v);
        var b = (%s.CastTest.B)v;
        """.formatted(pkg, pkg)).run());
  }

  @Test
  public void longToInt() {
    assertEquals(2, new Script().evalInt("Integer.bitCount(17l)"));
  }

  @Test
  public void stringToIntCast() {
    assertEquals(2, new Script().evalInt("Integer.bitCount(\"17\")"));
  }

  @Test
  public void stringToLongCast() {
    assertEquals(42, new Script().evalInt("long l = \"42\";l;"));
  }

  @Test
  public void stringToBooleanCast() {
    assertTrue(new Script().evalPredicate("boolean b = \"true\";b;"));
  }

  @Test
  public void stringToCharCast() {
    assertEquals('A', new Script().evalChar("char c = \"A\";c;"));
  }

}
