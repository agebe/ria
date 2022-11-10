package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    assertEquals(42, new Script().run("""
        var v = Integer::max;
        v(1,42);
        """));
  }

  @Test
  public void invokeSimple() {
    new Script().run("""
        var v = System.out::println;
        v(1);
        """);
  }

  @Test
  public void constructor() {
    LinkedList<?> l = (LinkedList<?>)new Script().run("""
        var v = java.util.LinkedList::new;
        v([1,2,3]);
        """);
    assertEquals(List.of(1,2,3), l);
  }

  @Test
  @Disabled
  public void stream() {
    HashSet<?> s = (HashSet<?>)new Script().run("""
        import java.util.*;
        import java.util.stream.*;
        [1,2,3,1,2].stream().collect(Collectors.toCollection(HashSet::new));
        """);
    assertEquals(Set.of(1,2,3), s);
  }

  @Test
  public void javaMethodReference() {
    //https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
    assertEquals("2", new Script().run("""
        java.util.List.of(42,2,99).stream()
          .filter(i -> i.equals(2))
          .map(Integer::toString)
          .findFirst()
          .orElse(null);
        """));
  }

}
