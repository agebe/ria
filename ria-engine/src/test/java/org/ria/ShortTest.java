package org.ria;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ShortTest {

  @Test
  public void simple() {
    new Script().run("short b = 1;");
  }

  @Test
  public void typeof() {
    assertEquals("short", new Script().run("short b = 1; typeof b;"));
  }

  @Test
  public void add() {
    assertEquals(2, new Script().evalShort("short b = 1; b+b;"));
  }

  @Test
  public void array() {
    assertEquals(2, new Script().run("var b = new short[2];b.length;"));
  }

  @Test
  @Disabled
  public void array2() {
    assertEquals(2, new Script().run("short[] b = new byte[2];b.length;"));
  }

  @Test
  public void arrayInit() {
    assertArrayEquals(new short[] {-1,1,2}, (short[])new Script().run("new short[] {-1,1,2}"));
  }

  @Test
  public void arrayInit2() {
    assertArrayEquals(new int[] {-1,1,2}, (int[])new Script().run("new int[] {-1,1,2}"));
  }

}
