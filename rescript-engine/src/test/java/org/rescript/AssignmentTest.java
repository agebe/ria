package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class AssignmentTest {

  @Test
  public void addAssign() {
    assertEquals("foobar", new Script().run("var a = 'foo';a+='bar';a;"));
  }

  @Test
  public void addAssign2() {
    assertEquals(42, new Script().run("var a = 21;a+=21;"));
  }

  @Test
  public void addAssignFail() {
    // variable a is unknown, must be initialized first
    assertThrows(ScriptException.class, () -> new Script().run("a += 21;"));
  }

}
