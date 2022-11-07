package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MethodReferenceTest {

  @Test
  public void objectMethodReference1() {
    assertEquals("method", new Script().run("""
        var v = System.out::println;
        typeof v;
        """));
  }

  @Test
  public void objectMethodReference2() {
    assertEquals(1, new Script().run("""
        var l = new java.util.ArrayList();
        l.add("1");
        var s = l::size;
        s();
        """));
  }

  @Test
  public void staticReference() {
    new Script().run("""
        var v = Integer::max;
        v(1,42);
        """);
  }

  @Test
  public void invokeSimple() {
    new Script().run("""
        var v = System.out::println;
        v(1);
        """);
  }

  @Test
  @Disabled
  public void constructor() {
    new Script().run("""
        var v = java.util.List::new;
        v(42);
        """);
  }

  @Test
  @Disabled
  public void javaMethodReference() {
    //https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
    assertEquals("2", new Script().run("""
        java.util.List.of(42,2,99).stream()
          .filter(i -> i.equals(2))
          //.map(i -> i.toString())
          .map(Integer::toString)
          .findFirst()
          .orElse(null);
        """));
  }

}
