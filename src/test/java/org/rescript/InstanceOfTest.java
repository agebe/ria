package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InstanceOfTest {

  // https://docs.oracle.com/en/java/javase/14/language/pattern-matching-instanceof-operator.html
  public static interface Shape { }

  public static class Rectangle implements Shape {
      final double length;
      final double width;
      
      public Rectangle(double length, double width) {
          this.length = length;
          this.width = width;
      }
      
      double length() { return length; }
      double width() { return width; }
  }

  public static class Circle implements Shape {
      final double radius;
      
      public Circle(double radius) {
          this.radius = radius;
      }
      
      double radius() { return radius; }
  }

  @Test
  public void isIstanceOf() {
    assertTrue(new Script().evalPredicate("\"\" instanceof String"));
  }

  @Test
  public void isIstanceOf2() {
    assertTrue(new Script().evalPredicate("""
        var s = new java.io.ByteArrayOutputStream();
        s instanceof java.io.OutputStream;
        """));
  }

//  @Test
//  @Disabled
//  public void isIstanceOf3() {
//    // does not work as 'int' is not resolved
//    assertTrue(new Script().evalPredicate("function f() {42;} 1 instanceof int;"));
//  }

  @Test
  public void isIstanceOf4() {
    assertTrue(new Script().evalPredicate("function f() {42;} 1 instanceof f();"));
  }

  @Test
  public void isIstanceOf5() {
    assertTrue(new Script().evalPredicate("1 instanceof 42"));
  }

  @Test
  public void isIstanceOf6() {
    assertTrue(new Script().evalPredicate("""
        import static %s.*;
        var circle = new Circle(1d);
        if(circle instanceof Rectangle s) {
          false;
        } else if(circle instanceof Circle s) {
          true;
        }
        """.formatted(this.getClass().getName())));
  }

  @Test
  public void isNotInstanceOf() {
    assertFalse(new Script().evalPredicate("1 instanceof String"));
  }

  @Test
  public void withBindVariable() {
    assertTrue(new Script().evalPredicate("\"foo\" instanceof String s"));
  }

  @Test
  public void withBindVariable2() {
    assertEquals("foo", new Script().run("\"foo\" instanceof String s;s;"));
  }

}
