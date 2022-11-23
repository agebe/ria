package org.rescript;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ByteTest {

  @Test
  public void simple() {
    new Script().run("byte b = 1;");
  }

  @Test
  public void typeof() {
    assertEquals("byte", new Script().run("byte b = 1; typeof b;"));
  }

  @Test
  public void add() {
    assertEquals(2, new Script().evalByte("byte b = 1; b+b;"));
  }

  @Test
  public void unaryMinus() {
    assertEquals(-1, new Script().evalByte("byte b = 1; -b;"));
  }

  @Test
  public void preInc() {
    assertEquals(2, new Script().evalByte("byte b = 1; ++b;"));
  }

  @Test
  public void postInc() {
    Script s = new Script();
    assertEquals(1, s.evalByte("byte b = 1; b++;"));
    assertEquals((byte)2, s.getVariable("b"));
  }

  @Test
  public void gt() {
    assertTrue(new Script().evalPredicate("byte b = 2; b > 1;"));
  }

  @Test
  public void gtFalse() {
    assertFalse(new Script().evalPredicate("byte b = 2; b > (byte)2;"));
  }

  @Test
  public void overflow() {
    assertEquals(-128, new Script().evalByte("byte b = 127; ++b;"));
  }

  @Test
  public void array() {
    assertEquals(2, new Script().run("var b = new byte[2];b.length;"));
  }

  @Test
  public void arrayInit() {
    assertArrayEquals(new byte[] {-1,1,2}, (byte[])new Script().run("new byte[] {-1,1,2}"));
  }

  @Test
  @Disabled
  // TODO add support for array declaration type
  public void array2() {
    assertEquals(2, new Script().run("byte[] b = new byte[2];b.length;"));
  }

}
