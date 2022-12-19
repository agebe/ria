package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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

}
