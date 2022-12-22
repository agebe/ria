package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ClassLiteralTest {

  @Test
  public void simple() {
    assertEquals(String.class, new Script().run("String.class"));
  }

  @Test
  public void string() {
    assertEquals(String.class, new Script().run("java.lang.String.class"));
  }

  @Test
  public void list() {
    assertEquals(List.class, new Script().run("List.class"));
  }

  @Test
  @Disabled
  public void listName() {
    System.out.println(List.class.getName());
    assertEquals("java.util.List", new Script().run("List.class.getName()"));
  }

  @Test
  public void floatDotClass() {
    assertEquals(float.class, new Script().run("float.class"));
  }

}
