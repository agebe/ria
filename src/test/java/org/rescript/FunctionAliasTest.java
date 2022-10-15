package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FunctionAliasTest {

  @Test
  public void alias1() {
    double d = new Script().evalDouble("""
        alias s java.lang.Math.sqrt;
        s(4d);
        """);
    assertEquals(2d, d);
  }

  @Test
  public void alias2() {
    double d = new Script().evalDouble("""
        alias s Math.sqrt;
        s(4d);
        """);
    assertEquals(2d, d);
  }


}
