package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ListTest {

  @Test
  public void simple() {
    assertEquals(List.of(1,2,3), new Script().run("java.util.List.of(1,2,3)"));
  }

  @Test
  public void listIndex() {
    assertEquals(2, new Script().run("java.util.List.of(1,2,3)[1]"));
  }


}
