package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ListTest {

  @Test
  public void literal() {
    assertEquals(List.of(1,2,3), new Script().run("[1,2,3]"));
  }

  @Test
  public void listOf() {
    assertEquals(List.of(1,2,3), new Script().run("java.util.List.of(1,2,3)"));
  }

  @Test
  public void listIndex() {
    assertEquals(2, new Script().run("java.util.List.of(1,2,3)[1]"));
  }

  @Test
  public void listLiteralIndex() {
    assertEquals(2, new Script().run("[1,2,3][1]"));
  }

  @Test
  public void listofLists() {
    assertEquals(List.of(List.of(42,43), List.of(1,2,3)), new Script().run("[[42,43],[1,2,3]]"));
  }

  @Test
  public void listLiteralIndexOutOfBounds() {
    assertThrows(ScriptException.class, () -> new Script().run("[1,2,3][3]"));
  }

}
