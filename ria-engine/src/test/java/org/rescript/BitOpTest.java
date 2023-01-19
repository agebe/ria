package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BitOpTest {

  @Test
  public void simpleAnd() {
    assertEquals(1, new Script().evalInt("9 & 5"));
  }

  @Test
  public void simpleOr() {
    assertEquals(13, new Script().evalInt("9 | 5"));
  }

  @Test
  public void simpleXor() {
    assertEquals(3, new Script().evalInt("6 ^ 5"));
  }

  @Test
  public void simpleNot() {
    assertEquals(-7, new Script().evalInt("~6"));
  }

  @Test
  public void leftShift() {
    assertEquals(10, new Script().evalInt("5 << 1"));
  }

  @Test
  public void rightShift() {
    assertEquals(5, new Script().evalInt("10 >> 1"));
  }

  @Test
  public void unsginedRightShift() {
    assertEquals(2, new Script().evalInt("10 >>> 2"));
  }

  @Test
  public void rightShift2() {
    assertEquals(-5, new Script().evalInt("-10 >> 1"));
  }

  @Test
  public void unsginedRightShift2() {
    assertEquals(3, new Script().evalInt("-10 >>> 30"));
  }

}
