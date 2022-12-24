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

}
