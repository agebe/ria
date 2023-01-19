package org.ria;

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

  @Test
  public void isIstanceOf3() {
    assertTrue(new Script().evalPredicate("function f() {42;} 1 instanceof int;"));
  }

  @Test
  public void isIstanceOfBoolean() {
    assertTrue(new Script().evalPredicate("true instanceof boolean"));
  }

  @Test
  public void isIstanceOfBoolean2() {
    assertFalse(new Script().evalPredicate("true instanceof Boolean"));
  }

  @Test
  public void isIstanceOfBoolean3() {
    assertTrue(new Script().evalPredicate("Boolean.TRUE instanceof Boolean"));
  }

  @Test
  public void isIstanceOf5() {
    assertTrue(new Script().evalPredicate("1f instanceof float"));
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
